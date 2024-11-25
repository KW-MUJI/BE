package com.muji_backend.kw_muji.team.repository;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findAllByIsOnGoing(boolean isOnGoing, Sort sort);
    List<ProjectEntity> findAllByIsOnGoingTrueAndDeadlineAtLessThan(LocalDateTime date);
}
