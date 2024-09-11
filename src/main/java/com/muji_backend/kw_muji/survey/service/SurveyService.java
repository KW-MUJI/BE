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

    // 한 페이지에 보여줄 최대 설문 조사 항목 수 8개로 고정
    private static final int PAGE_SIZE = 8;

    @Autowired
    private SurveyRepository surveyRepository;

    /**
     * 설문 조사 목록을 검색하고, 페이지 번호를 기반으로 페이징 처리하여 반환하는 메서드
     *
     * @param search 검색어 (설문 제목 또는 설명에서 필터링)
     * @param page   페이지 번호 (0부터 시작)
     */
    public SurveyResponseDto getSurveys(String search, int page) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        Page<SurveyEntity> surveyPage;

        // 검색어가 없거나 공백일 경우, 전체 설문 조사를 조회
        if (search == null || search.isBlank()) {
            surveyPage = surveyRepository.findAll(pageRequest);
        } else { // 제목 또는 설명에 검색어가 포함된 설문 조사를 조회
            surveyPage = surveyRepository.findByTitleContainingOrDescriptionContaining(search, search, pageRequest);
        }

        List<SurveyResponseDto.SurveyItemDto> surveyItems = surveyPage.getContent().stream()
                .map(survey -> SurveyResponseDto.SurveyItemDto.builder()
                        .surveyId(survey.getId())
                        .title(survey.getTitle())
                        .description(survey.getDescription())
                        .createdAt(survey.getCreatedAt().toLocalDate())
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
