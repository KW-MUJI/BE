package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.survey.dto.response.SurveyResponseDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private static final int PAGE_SIZE = 8;

    @Autowired
    private SurveyRepository surveyRepository;

    public SurveyResponseDto getSurveys(String search, int page) {

    }
}
