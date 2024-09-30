package com.muji_backend.kw_muji.project.repository;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
