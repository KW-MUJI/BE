package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.survey.dto.response.SurveyResponseDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private static final int PAGE_SIZE = 8;

    @Autowired
    private SurveyRepository surveyRepository;

    public SurveyResponseDto getSurveys(String search, int page) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        Page<SurveyEntity> surveyPage;

        if (search == null || search.isBlank()) {
            surveyPage = surveyRepository.findAll(pageRequest);
        } else {
        }

        List<SurveyResponseDto.SurveyItemDto> surveyItems = surveyPage.getContent().stream()
                .map(survey -> SurveyResponseDto.SurveyItemDto.builder()
                        .surveyId(survey.getId())
                        .title(survey.getTitle())
                        .description(survey.getDescription())
                        .createdAt(survey.getCreatedAt())
                        .endDate(survey.getEndDate())
                        .build())
                .collect(Collectors.toList());

        return SurveyResponseDto.builder()
                .currentPage(surveyPage.getNumber())
                .totalPages(surveyPage.getTotalPages())
                .totalItems(surveyPage.getTotalElements())
                .surveys(surveyItems)
                .build();
    }
}
