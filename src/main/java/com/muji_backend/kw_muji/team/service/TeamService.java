package com.muji_backend.kw_muji.team.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.ResumeEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.mypage.repository.ResumeRepository;
import com.muji_backend.kw_muji.team.dto.response.ResumeResponseDTO;
import com.muji_backend.kw_muji.team.repository.RoleRepository;
import com.muji_backend.kw_muji.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class TeamService {
    private final TeamRepository projectRepo;
    private final RoleRepository roleRepo;
    private final ResumeRepository resumeRepo;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName2}")
    private String projectImageBucketFolder; // aws에 추가하기

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }

    // 팀 프로젝트 글 수정 시, 사용하기
//    public void deleteProjectImage(final String email) {
//        if(projectRepo.findByEmail(email).getImage() != null) {
//            final String formalS3Key = mypageRepo.findByEmail(email).getImage();
//
//            if(amazonS3.doesObjectExist(bucket, formalS3Key)) {
//                amazonS3.deleteObject(bucket, formalS3Key);
//            }
//        }
//    }

    public String uploadProjectImage(final MultipartFile[] files, final String title) throws IOException {
        if(files.length > 1) {
            throw new IllegalArgumentException("프로젝트 이미지가 1개를 초과함");
        }

        if(!Objects.equals(files[0].getContentType(), "image/jpeg") && !Objects.equals(files[0].getContentType(), "image/jpeg")) {
            throw new IllegalArgumentException("프로젝트 이미지의 타입이 jpg가 아님");
        }

        // 파일 이름 가공
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final Date time = new Date();
        final String name = files[0].getOriginalFilename();
        final String[] fileName = new String[]{Objects.requireNonNull(name).substring(0, name.length() - 4)};

        // S3 Key 구성
        final String S3Key = projectImageBucketFolder + fileName[0] + "\\" + title + "\\" + dateFormat.format(time) + ".jpg";

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(files[0].getSize());
        metadata.setContentType(files[0].getContentType());

        // 저장
        amazonS3.putObject(bucket, S3Key, files[0].getInputStream(), metadata);

        return S3Key;
    }

    public void registerProject(ProjectEntity entity) {
        projectRepo.save(entity);
    }

    public ProjectEntity getProject(final Long projectId) {
        final ProjectEntity project = projectRepo.findById(projectId).orElse(null);

        if(project == null)
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");

        return project;
    }

    public ParticipationEntity getRole(final Long projectId, final UserEntity user) {
        return roleRepo.findByProjectIdAndUsers(projectId, user);
    }

    public List<ResumeResponseDTO> getAllResumes(final UserEntity user) {
        return resumeRepo.findAllByUsers(user).stream()
                .map(entity -> new ResumeResponseDTO(entity.getId(), entity.getName(), entity.getCreatedAt()))
                .toList();
    }

    public ResumeEntity getResume(final Long resumeId) {
        return resumeRepo.findById(resumeId).orElse(null);
    }

    public void saveParticipation(final ParticipationEntity participation, final ProjectEntity project) {
        final ParticipationEntity user = roleRepo.findByProjectIdAndUsers(project.getId(), participation.getUsers());

        if(user.getRole() == ProjectRole.APPLICANT)
            throw new IllegalArgumentException("중복 지원 불가");

        if(user.getRole() == ProjectRole.CREATOR)
            throw new IllegalArgumentException("본인이 생성한 글");

        roleRepo.save(participation);
    }
}
