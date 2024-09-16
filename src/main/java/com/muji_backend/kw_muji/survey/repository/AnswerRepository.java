package com.muji_backend.kw_muji.survey.repository;

import com.muji_backend.kw_muji.common.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
}