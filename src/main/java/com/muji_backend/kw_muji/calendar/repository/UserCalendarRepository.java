package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.UserCalendarEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserCalendarRepository extends JpaRepository<UserCalendarEntity, Long> {
    // 개인 일정 조회 (projectId가 null인 경우)
    List<UserCalendarEntity> findAllByUsersAndProjectIsNullAndEventDateBetween(UserEntity user, LocalDateTime startDate, LocalDateTime endDate);

    // 프로젝트 일정 조회 (projectId가 존재하는 경우)
    List<UserCalendarEntity> findAllByProjectAndEventDateBetween(ProjectEntity project, LocalDateTime startDate, LocalDateTime endDate);
}
