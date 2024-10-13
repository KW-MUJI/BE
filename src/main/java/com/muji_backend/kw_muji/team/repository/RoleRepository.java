package com.muji_backend.kw_muji.team.repository;

import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<ParticipationEntity, Long> {
    ParticipationEntity findByProjectIdAndUsers(Long projectId, UserEntity user);
    List<ParticipationEntity> findAllByUsersAndRole(UserEntity user, ProjectRole role);
    List<ParticipationEntity> findAllByProjectAndRole(ProjectEntity project, ProjectRole role);
}
