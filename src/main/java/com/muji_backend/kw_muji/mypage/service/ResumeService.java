package com.muji_backend.kw_muji.mypage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.muji_backend.kw_muji.common.entity.ResumeEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.mypage.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class ResumeService {
    private final ResumeRepository resumeRepo;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName3}")
    private String resumeBucketFolder;

    public String uploadResume(final MultipartFile[] files, final String userName, final ResumeEntity resume) throws IOException {
        if(files[0].isEmpty())
            throw new IllegalArgumentException("선택된 포트폴리오가 없음");
        else if(files.length > 1)
            throw new IllegalArgumentException("파일 수가 1개를 초과함");

        if(!Objects.equals(files[0].getContentType(), "application/pdf"))
            throw new IllegalArgumentException("PDF가 아님");

        // 파일 이름 가공
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final Date time = new Date();
        final String name = files[0].getOriginalFilename();
        final String[] fileName = new String[]{Objects.requireNonNull(name).substring(0, name.length() - 4)};

        // S3 Key 구성
        final String S3Key = resumeBucketFolder + fileName[0] + "\\" + userName + "\\" + dateFormat.format(time) + ".pdf";

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(files[0].getSize());
        metadata.setContentType("application/pdf");

        // 저장
        amazonS3.putObject(bucket, S3Key, files[0].getInputStream(), metadata);
        resume.setName(fileName[0]);

        return S3Key;
    }

    public void saveResume(final ResumeEntity resume) {
        resumeRepo.save(resume);
    }

    public void deleteResumeFile(final ResumeEntity resume) {
        final String formalS3Key = resume.getResumePath();
        if(amazonS3.doesObjectExist(bucket, formalS3Key))
            amazonS3.deleteObject(bucket, formalS3Key);
    }

    public ResumeEntity getResume(final Long resumeId, final UserEntity user) {
        if(resumeRepo.findByUsersAndId(user, resumeId) == null)
            throw new IllegalArgumentException("포트폴리오 조회 오류");

        return resumeRepo.findByUsersAndId(user, resumeId);
    }

    @Transactional
    public void deleteResume(final ResumeEntity resume) {
        deleteResumeFile(resume);
        resumeRepo.delete(resume);
    }

    public boolean checkResumeCount(final UserEntity user) {
        return resumeRepo.countByUsers(user) < 3;
    }
}
