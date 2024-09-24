package com.muji_backend.kw_muji.survey.repository;

import com.muji_backend.kw_muji.common.entity.ResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
    List<ResponseEntity> findBySurveyId(Long surveyId);
    boolean existsByUsersIdAndSurveyId(Long userId, Long surveyId);
}