package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<ParticipationEntity, Long> {
    // 유저가 참여 중인 프로젝트를 조회 (Role이 CREATOR 또는 MEMBER인 경우, 그리고 프로젝트 시작 값이 true인 경우)
    List<ParticipationEntity> findAllByUsersAndRoleInAndProjectStartTrue(UserEntity user, List<ProjectRole> roles);
}