package com.muji_backend.kw_muji.team.repository;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<ProjectEntity, Long> {
}
