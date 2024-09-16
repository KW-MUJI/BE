package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.ChoiceEntity;
import com.muji_backend.kw_muji.common.entity.QuestionEntity;
import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.QuestionType;
import com.muji_backend.kw_muji.survey.dto.request.SurveyRequestDto;
import com.muji_backend.kw_muji.survey.dto.response.SurveyDetailResponseDto;
import com.muji_backend.kw_muji.survey.dto.response.SurveyResponseDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    /**
     * 설문조사의 상세 정보를 반환하는 메서드
     *
     * @param surveyId 설문조사 ID
     */
    public SurveyDetailResponseDto getSurveyDetail(Long surveyId) {
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사가 존재하지 않습니다: " + surveyId));

        return SurveyDetailResponseDto.builder()
                .surveyId(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createdAt(survey.getCreatedAt().toLocalDate())
                .endDate(survey.getEndDate())
                .questions(survey.getQuestion().stream()
                        .map(question -> SurveyDetailResponseDto.QuestionDto.builder()
                                .questionId(question.getId())
                                .questionText(question.getQuestionText())
                                .questionType(question.getQuestionType())
                                .choices(question.getChoice().stream()
                                        .map(choice -> SurveyDetailResponseDto.QuestionDto.ChoiceDto.builder()
                                                .choiceId(choice.getId())
                                                .choiceText(choice.getChoiceText())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }
}
