package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.UserCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCalendarRepository extends JpaRepository<UserCalendarEntity, Long> {
}
