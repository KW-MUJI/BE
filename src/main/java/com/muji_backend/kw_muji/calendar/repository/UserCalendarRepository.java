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

    // 프로젝트 일정 조회 (projectId가 있는 경우 / 프로젝트 리스트와 날짜 범위에 따라 일정 조회)
    List<UserCalendarEntity> findAllByProjectInAndEventDateBetween(List<ProjectEntity> projects, LocalDateTime startDate, LocalDateTime endDate);
    // 동일한 프로젝트, 동일한 날짜, 동일한 제목을 가진 팀원의 일정을 조회
    List<UserCalendarEntity> findAllByProjectAndEventDateAndTitle(ProjectEntity project, LocalDateTime eventDate, String title);
}
