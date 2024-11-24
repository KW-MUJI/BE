package com.muji_backend.kw_muji.team.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.team.dto.request.ProjectDetailRequestDTO;
import com.muji_backend.kw_muji.team.dto.response.ApplicantResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MemberResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyCreatedProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import com.muji_backend.kw_muji.team.repository.RoleRepository;
import com.muji_backend.kw_muji.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class MyTeamService {
    private final RoleRepository roleRepo;
    private final TeamRepository teamRepo;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName2}")
    private String projectImageBucketFolder;

    public List<MyProjectResponseDTO> getMyProjects(final UserEntity user) {
        final List<ParticipationEntity> participationList = roleRepo.findAllByUsersAndRole(user, ProjectRole.MEMBER); // 내가 맴버로 참가한 참가자 리스트

        return participationList.stream().map(list -> {
            final MyProjectResponseDTO myProjectResponseDTO = new MyProjectResponseDTO();
            myProjectResponseDTO.setId(list.getProject().getId());
            myProjectResponseDTO.setName(list.getProject().getName());

            final List<MemberResponseDTO> members = new ArrayList<>();
            final List<ParticipationEntity> participations = new ArrayList<>();
            participations.addAll(roleRepo.findAllByProjectAndRole(list.getProject(), ProjectRole.CREATOR));
            participations.addAll(roleRepo.findAllByProjectAndRole(list.getProject(), ProjectRole.MEMBER));

            for(ParticipationEntity participation : participations) {
                final MemberResponseDTO member = MemberResponseDTO.builder()
                        .image(participation.getUsers().getImage())
                        .name(participation.getUsers().getName())
                        .stuNum(participation.getUsers().getStuNum())
                        .major(participation.getUsers().getMajor())
                        .email(participation.getUsers().getEmail())
                        .build();
                members.add(member);
            }

            myProjectResponseDTO.setMembers(members);
            return myProjectResponseDTO;
        }).toList();
    }

    public List<MyCreatedProjectResponseDTO> getMyCreatedProjects(final UserEntity user) {
        final List<ParticipationEntity> participationList = roleRepo.findAllByUsersAndRole(user, ProjectRole.CREATOR); // 내가 생성한 프로젝트

        return participationList.stream().map(list -> {
            final MyCreatedProjectResponseDTO myCreatedProjectResponseDTO = new MyCreatedProjectResponseDTO();
            myCreatedProjectResponseDTO.setId(list.getProject().getId());
            myCreatedProjectResponseDTO.setName(list.getProject().getName());
            myCreatedProjectResponseDTO.setIsOnGoing(list.getProject().isOnGoing());

            final List<ApplicantResponseDTO> members = new ArrayList<>();
            final List<ParticipationEntity> applicants = new ArrayList<>();
            applicants.addAll(roleRepo.findAllByProjectAndRole(list.getProject(), ProjectRole.APPLICANT));

            for(ParticipationEntity applicant : applicants) {
                final ApplicantResponseDTO member = ApplicantResponseDTO.builder()
                        .id(applicant.getId())
                        .image(applicant.getUsers().getImage())
                        .name(applicant.getUsers().getName())
                        .stuNum(applicant.getUsers().getStuNum())
                        .major(applicant.getUsers().getMajor())
                        .resume(applicant.getResumePath())
                        .build();
                members.add(member);
            }

            myCreatedProjectResponseDTO.setApplicants(members);
            return myCreatedProjectResponseDTO;
        }).toList();
    }

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }

    public void selectApplicant(final Long memberId) {
        final Optional<ParticipationEntity> applicant = roleRepo.findById(memberId);

        if(!applicant.isPresent() || applicant.get().getRole().equals(ProjectRole.CREATOR))
            throw new IllegalArgumentException("확인되지 않은 유저입니다.");

        if(applicant.get().getRole().equals(ProjectRole.MEMBER))
            throw new IllegalArgumentException("이미 선택한 유저입니다.");

        if(applicant.get().getRole().equals(ProjectRole.APPLICANT))
            applicant.get().setRole(ProjectRole.MEMBER);

        roleRepo.save(applicant.get());
    }

    public void deleteProject(final Long projectId, final UserEntity user) {
        if (!roleRepo.findByProjectIdAndUsers(projectId, user).getRole().equals(ProjectRole.CREATOR))
            throw new IllegalArgumentException("삭제 권한 없음");

        if (!teamRepo.findById(projectId).isPresent())
            throw new IllegalArgumentException("존재하지 않는 프로젝트");

        final ProjectEntity project = teamRepo.findById(projectId).get();
        teamRepo.delete(project);
    }

    public boolean isMyProject(final Long projectId, final UserEntity user) {
        return roleRepo.findByProjectIdAndUsers(projectId, user).getRole().equals(ProjectRole.CREATOR);
    }

    public void deleteProjectImage(final Long projectId) {
        if(teamRepo.findById(projectId).get().getImage() != null) {
            final String formalS3Key = teamRepo.findById(projectId).get().getImage();

            if(amazonS3.doesObjectExist(bucket, formalS3Key))
                amazonS3.deleteObject(bucket, formalS3Key);
        }
    }

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

    @Transactional
    public void updateProject(final ProjectEntity project, final ProjectDetailRequestDTO dto) {
        if(dto.getName() != null && !dto.getName().isBlank())
            project.setName(dto.getName());

        if(dto.getDescription() != null && !dto.getDescription().isBlank())
            project.setDescription(dto.getDescription());

        if(!dto.isDeleteImage() && project.getImage() != null && !project.getImage().isBlank())
            project.setImage(project.getImage());
        else if(dto.isDeleteImage())
            project.setImage(null);

        if(dto.getDeadlineAt() != null)
            project.setDeadlineAt(dto.getDeadlineAt().atStartOfDay());

       teamRepo.save(project);
    }

    public void updateStart(final Long projectId) {
        final ProjectEntity project = teamRepo.findById(projectId).get();

        if(!project.isStart())
            project.setStart(true);
        else
            throw new IllegalArgumentException("이미 시작한 프로젝트");

        teamRepo.save(project);
    }
}
