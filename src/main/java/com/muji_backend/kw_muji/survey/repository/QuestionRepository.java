package com.muji_backend.kw_muji.survey.repository;

import com.muji_backend.kw_muji.common.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
}