package com.muji_backend.kw_muji.mypage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ResumeEntity;
import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.mypage.dto.request.UpdateRequestDTO;
import com.muji_backend.kw_muji.mypage.dto.response.MyProjectsResponseDTO;
import com.muji_backend.kw_muji.mypage.dto.response.MyResponseDTO;
import com.muji_backend.kw_muji.mypage.repository.MypageRepository;
import com.muji_backend.kw_muji.mypage.repository.ResumeRepository;
import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.survey.service.MySurveyService;
import com.muji_backend.kw_muji.team.dto.response.MyCreatedProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.ResumeResponseDTO;
import com.muji_backend.kw_muji.team.repository.RoleRepository;
import com.muji_backend.kw_muji.team.service.MyTeamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Service
public class MypageService {
    private final MyTeamService myTeamService;

    private final SurveyRepository surveyRepository;
    private final MypageRepository mypageRepo;
    private final RoleRepository roleRepo;
    private final ResumeRepository resumeRepo;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName1}")
    private String userImageBucketFolder;

    @Value("${cloud.aws.s3.url}")
    private String bucketURL;

    public Boolean equalPassword(final String email, final String password, final PasswordEncoder encoder) {
        final UserEntity user = mypageRepo.findByEmail(email);

        return user != null && encoder.matches(password, user.getPassword());
    }

    public UserEntity originalUser(final String email) {
        return mypageRepo.findByEmail(email);
    }

    @Transactional
    public UserEntity updateUser(final UserEntity userEntity, final UpdateRequestDTO dto) {
        final UserEntity user = originalUser(userEntity.getEmail());

        if (dto.getName() != null && !dto.getName().isBlank())
            user.setName(dto.getName());

        if (dto.getStuNum() > 0)
            user.setStuNum(dto.getStuNum());

        if (dto.getMajor() != null && !dto.getMajor().isBlank())
            user.setMajor(dto.getMajor());

        if (userEntity.getPassword() != null && !userEntity.getPassword().isBlank())
            user.setPassword(userEntity.getPassword());

        if (!dto.isDeleteImage() && userEntity.getImage() != null && !userEntity.getImage().isBlank())
            user.setImage(userEntity.getImage());
        else if (dto.isDeleteImage())
            user.setImage(null);

        return mypageRepo.save(user);
    }

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }

    public void deleteUserImage(final String email) {
        if (mypageRepo.findByEmail(email).getImage() != null) {
            final String formalS3Key = mypageRepo.findByEmail(email).getImage();

            if (amazonS3.doesObjectExist(bucket, formalS3Key))
                amazonS3.deleteObject(bucket, formalS3Key);
        }
    }

    public String uploadUserImage(final MultipartFile[] files, final String userName, final String email) throws IOException {
        if (files.length > 1)
            throw new IllegalArgumentException("프로필 이미지가 1개를 초과함");

        if (!Objects.equals(files[0].getContentType(), "image/jpeg") && !Objects.equals(files[0].getContentType(), "image/jpeg"))
            throw new IllegalArgumentException("프로필 이미지의 타입이 jpg가 아님");

        // 파일 이름 가공
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final Date time = new Date();
        final String name = files[0].getOriginalFilename();
        final String[] fileName = new String[]{Objects.requireNonNull(name).substring(0, name.length() - 4)};

        // S3 Key 구성
        final String S3Key = userImageBucketFolder + fileName[0] + "\\" + userName + "\\" + dateFormat.format(time) + ".jpg";

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(files[0].getSize());
        metadata.setContentType(files[0].getContentType());

        // 기존 파일 삭제
        deleteUserImage(email);

        // 저장
        amazonS3.putObject(bucket, S3Key, files[0].getInputStream(), metadata);

        return S3Key;
    }

    @Transactional
    public void deleteUser(final UserEntity user) {
        deleteUserImage(user.getEmail());
        mypageRepo.delete(user);
    }

    /**
     * 마이페이지 메인에 표시될 정보를 조회
     * 1. 참여 중인 팀 프로젝트의 최신 4개 목록 (MEMBER 또는 CREATOR이며 시작된 프로젝트)
     * 2. 사용자가 생성한 프로젝트 중 모집 중인 최신 4개 목록 (CREATOR인 프로젝트)
     * 3. 사용자가 작성한 설문조사 중 가장 최근의 4개 목록
     *
     * @param user 사용자 엔티티
     * @return MyResponseDTO
     */
    public MyResponseDTO getMyPageInfo(UserEntity user) throws UnsupportedEncodingException {
        // 내 정보
        MyResponseDTO.MyProfile myProfile = getMyProfile(user);

        // my 팀플 최신 4개
        List<String> projects = getMyProjects(user);

        // my 모집 팀플 최신 4개
        List<MyResponseDTO.MyCreatedProject> createdProjects = getMyCreatedProjects(user);

        // my 설문조사 최신 4개 제목
        List<MyResponseDTO.MySurvey> surveys = getMySurvey(user);

        // 포트폴리오
        List<MyResponseDTO.Resume> resumes = getMyResumes(user);

        // 지원한 팀플
        List<MyResponseDTO.applicationProject> applicationProjects = getMyApplicationProjects(user);

        return MyResponseDTO.builder()
                .profile(myProfile)
                .projects(projects)
                .createdProjects(createdProjects)
                .surveys(surveys)
                .resumes(resumes)
                .applicationProjects(applicationProjects)
                .build();
    }

    // == Private Methods ==

    // 내 정보를 불러오는 메서드
    private MyResponseDTO.MyProfile getMyProfile(UserEntity user) throws UnsupportedEncodingException {
        UserEntity userInfo = originalUser(user.getEmail());

        return MyResponseDTO.MyProfile.builder()
                .userId(userInfo.getId())
                .userImage(userInfo.getImage() != null ? bucketURL + URLEncoder.encode(userInfo.getImage(), "UTF-8") : "")
                .username(userInfo.getName())
                .build();
    }

    // 최근 설문조사 4개만 조회하는 메서드
    private List<MyResponseDTO.MySurvey> getMySurvey(UserEntity user) {
        List<SurveyEntity> surveys = surveyRepository.findByUsers(user);

        List<MyResponseDTO.MySurvey> mySurveys =
                surveys.stream()
                        .map(survey -> MyResponseDTO.MySurvey.builder()
                                .surveyId(survey.getId())
                                .title(survey.getTitle())
                                .endDate(survey.getEndDate())
                                .isOngoing(survey.isOngoing())
                                .build())
                        .toList();

        return mySurveys.stream()
                .sorted(Comparator.comparing(MyResponseDTO.MySurvey::getEndDate).reversed())
                .limit(4)
                .toList();
    }

    // my 팀플 조회 메소드 - MEMBER인 프로젝트 & CREATOR이며 start가 true인 프로젝트
    private List<String> getMyProjects(UserEntity user) {
        List<ParticipationEntity> memberProjects = roleRepo.findAllByUsersAndRole(user, ProjectRole.MEMBER);
        List<ParticipationEntity> creatorProjects = roleRepo.findAllByUsersAndRole(user, ProjectRole.CREATOR).stream()
                .filter(project -> project.getProject().isStart())
                .toList();

        List<ParticipationEntity> joined = Stream.concat(memberProjects.stream(), creatorProjects.stream())
                .sorted(Comparator.comparing(project -> project.getProject().getDeadlineAt(), Comparator.reverseOrder())) // 마감일 기준 최신 4개 반환
                .limit(4)
                .toList();

        return joined.stream()
                .map(project -> project.getProject().getName())
                .toList();
    }

    // my 모집 팀플 조회 메소드 - CREATOR인 프로젝트
    private List<MyResponseDTO.MyCreatedProject> getMyCreatedProjects(UserEntity user) {
        List<ParticipationEntity> creatorProjects = roleRepo.findAllByUsersAndRole(user, ProjectRole.CREATOR);

        return creatorProjects.stream()
                .map(project -> MyResponseDTO.MyCreatedProject.builder()
                        .name(project.getProject().getName())
                        .deadlineAt(project.getProject().getDeadlineAt())
                        .isOngoing(project.getProject().isOnGoing())
                        .build())
                .sorted(Comparator.comparing(MyResponseDTO.MyCreatedProject::getDeadlineAt).reversed()) // 최신 모집 팀플 4개 조회
                .limit(4)
                .toList();
    }

    // 포트폴리오 조회 메소드
    private List<MyResponseDTO.Resume> getMyResumes(UserEntity user) {
        List<ResumeEntity> resumes = resumeRepo.findAllByUsers(user);

        return resumes.stream()
                .map(resume -> MyResponseDTO.Resume.builder()
                        .resumeId(resume.getId())
                        .name(resume.getName())
                        .createdAt(resume.getCreatedAt())
                        .build())
                .toList();
    }

    // 지원한 팀플
    private List<MyResponseDTO.applicationProject> getMyApplicationProjects(UserEntity user) {
        List<ParticipationEntity> applicationProjects = roleRepo.findAllByUsersAndRole(user, ProjectRole.APPLICANT);

        return applicationProjects.stream()
                .map(project -> MyResponseDTO.applicationProject.builder()
                        .name(project.getProject().getName())
                        .applicantsNum(roleRepo.countByProjectAndRole(project.getProject(), ProjectRole.APPLICANT))
                        .deadlineAt(project.getProject().getDeadlineAt())
                        .build())
                .toList();
    }
}
