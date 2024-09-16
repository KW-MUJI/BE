package com.muji_backend.kw_muji.survey.repository;

import com.muji_backend.kw_muji.common.entity.ResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
}