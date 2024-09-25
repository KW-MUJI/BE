package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.UnivCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnivCalendarRepository extends JpaRepository<UnivCalendarEntity, Long> {
}