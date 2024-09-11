package com.muji_backend.kw_muji.survey.repository;

import com.muji_backend.kw_muji.common.entity.ChoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<ChoiceEntity, Long> {
}