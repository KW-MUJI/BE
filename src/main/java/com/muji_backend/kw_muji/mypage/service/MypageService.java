package com.muji_backend.kw_muji.mypage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ResumeEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.mypage.dto.request.UpdateRequestDTO;
import com.muji_backend.kw_muji.mypage.dto.response.MyProjectsResponseDTO;
import com.muji_backend.kw_muji.mypage.repository.MypageRepository;
import com.muji_backend.kw_muji.mypage.repository.ResumeRepository;
import com.muji_backend.kw_muji.team.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class MypageService {
    private final MypageRepository mypageRepo;
    private final RoleRepository roleRepo;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName1}")
    private String userImageBucketFolder;

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

        if(dto.getName() != null && !dto.getName().isBlank())
            user.setName(dto.getName());

        if(dto.getStuNum() > 0)
            user.setStuNum(dto.getStuNum());

        if(dto.getMajor() != null && !dto.getMajor().isBlank())
            user.setMajor(dto.getMajor());

        if(userEntity.getPassword() != null && !userEntity.getPassword().isBlank())
            user.setPassword(userEntity.getPassword());

        if(!dto.isDeleteImage() && userEntity.getImage() != null && !userEntity.getImage().isBlank())
            user.setImage(userEntity.getImage());
        else if(dto.isDeleteImage())
            user.setImage(null);

        return mypageRepo.save(user);
    }

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }

    public void deleteUserImage(final String email) {
        if(mypageRepo.findByEmail(email).getImage() != null) {
            final String formalS3Key = mypageRepo.findByEmail(email).getImage();

            if(amazonS3.doesObjectExist(bucket, formalS3Key))
                amazonS3.deleteObject(bucket, formalS3Key);
        }
    }

    public String uploadUserImage(final MultipartFile[] files, final String userName, final String email) throws IOException {
        if(files.length > 1)
            throw new IllegalArgumentException("프로필 이미지가 1개를 초과함");

        if(!Objects.equals(files[0].getContentType(), "image/jpeg") && !Objects.equals(files[0].getContentType(), "image/jpeg"))
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

    public List<MyProjectsResponseDTO> getMyProjects(final UserEntity user) {
        final List<ParticipationEntity> projectList = roleRepo.findAllByUsersAndRole(user, ProjectRole.MEMBER);

        return projectList.stream().map(list -> {
            final MyProjectsResponseDTO myProjectsResponseDTO = new MyProjectsResponseDTO();
            myProjectsResponseDTO.setName(list.getProject().getName());

            return myProjectsResponseDTO;
        }).toList();
    }

    public List<MyProjectsResponseDTO> getMyCreatedProjects(final UserEntity user) {
        final List<ParticipationEntity> projectList = roleRepo.findAllByUsersAndRole(user, ProjectRole.CREATOR);

        return projectList.stream().map(list -> {
            final MyProjectsResponseDTO myProjectsResponseDTO = new MyProjectsResponseDTO();
            myProjectsResponseDTO.setName(list.getProject().getName());

            return myProjectsResponseDTO;
        }).toList();
    }
}
