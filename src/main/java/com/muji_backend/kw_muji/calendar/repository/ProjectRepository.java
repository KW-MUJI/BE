package com.muji_backend.kw_muji.calendar.repository;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}