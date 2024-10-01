package com.muji_backend.kw_muji.mypage.repository;

import com.muji_backend.kw_muji.common.entity.ResumeEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
    ResumeEntity findByUsersAndId(UserEntity user, Long id);
    List<ResumeEntity> findAllByUsers(UserEntity user);
}
