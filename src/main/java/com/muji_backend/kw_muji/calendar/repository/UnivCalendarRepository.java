package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.UnivCalendarEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UnivCalendarRepository extends JpaRepository<UnivCalendarEntity, Long> {
    // 대학 일정 조회
    List<UnivCalendarEntity> findAllByUsersAndEventDateBetween(UserEntity user, LocalDateTime startDate, LocalDateTime endDate);}