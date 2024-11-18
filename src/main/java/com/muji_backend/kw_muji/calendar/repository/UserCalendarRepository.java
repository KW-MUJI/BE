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
}
