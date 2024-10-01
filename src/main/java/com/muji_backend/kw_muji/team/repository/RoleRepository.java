package com.muji_backend.kw_muji.team.repository;

import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<ParticipationEntity, Long> {
    ParticipationEntity findByProjectIdAndUsers(Long projectId, UserEntity user);
}
