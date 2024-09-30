package com.muji_backend.kw_muji.project.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.muji_backend.kw_muji.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName2}")
    private String projectImageBucketFolder; // aws에 추가하기

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }

//    public void deleteProjectImage(final String email) {
//        if(projectRepo.findByEmail(email).getImage() != null) {
//            final String formalS3Key = mypageRepo.findByEmail(email).getImage();
//
//            if(amazonS3.doesObjectExist(bucket, formalS3Key)) {
//                amazonS3.deleteObject(bucket, formalS3Key);
//            }
//        }
//    }

    public String uploadProjectImage(final MultipartFile[] files, final String title, final String email) throws IOException {
        if(files.length > 1) {
            throw new IllegalArgumentException("프로필 이미지가 1개를 초과함");
        }

        if(!Objects.equals(files[0].getContentType(), "image/jpeg") && !Objects.equals(files[0].getContentType(), "image/jpeg")) {
            throw new IllegalArgumentException("프로필 이미지의 타입이 jpg가 아님");
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

        // 기존 파일 삭제 - 편집 없을 경우 없어도 됨
//        deleteProjectImage(email);

        // 저장
        amazonS3.putObject(bucket, S3Key, files[0].getInputStream(), metadata);

        return S3Key;
    }
}
