package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.UserEventLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEventLinkRepository extends JpaRepository<UserEventLinkEntity, Long> {
    // 개인 일정 조회 (projectId가 null인 경우)
    List<UserEventLinkEntity> findAllByUsersAndProjectIsNull(UserEntity user);

    // 팀플 일정 조회 (projectId가 있는 경우)
    List<UserEventLinkEntity> findAllByProject(ProjectEntity project);

    // 해당 사용자가 특정 일정 링크를 가지고 있는지 조회
    Optional<UserEventLinkEntity> findByUsersAndUserCalendarId(UserEntity user, Long userCalendarId);
}
