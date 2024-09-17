package com.muji_backend.kw_muji.survey.repository;

import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    Page<SurveyEntity> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);
}