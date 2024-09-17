package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MySurveyService {

    public List<MySurveyResponseDto> getSurveysByUserId(Long userId) {

    }
}