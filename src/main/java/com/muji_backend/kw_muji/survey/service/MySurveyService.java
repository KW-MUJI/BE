package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MySurveyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    /**
     * 특정 유저가 작성한 설문조사 목록을 조회
     *
     * @param userId 유저 ID
     * @return 해당 유저가 작성한 설문조사 목록
     */
    public List<MySurveyResponseDto> getSurveysByUserId(Long userId) {
        UserEntity user = getUserById(userId);

        List<SurveyEntity> surveys = surveyRepository.findByUsers(user);

        return surveys.stream()
                .map(this::mapToMySurveyResponseDto)
                .collect(Collectors.toList());
    }

    // == Private Methods ==

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));
    }

    private MySurveyResponseDto mapToMySurveyResponseDto(SurveyEntity survey) {
        return MySurveyResponseDto.builder()
                .surveyId(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .isOngoing(survey.isOngoing())
                .createdAt(survey.getCreatedAt().toLocalDate())
                .build();
    }

    /**
     * 설문조사를 종료하고 상태를 업데이트하는 메서드
     *
     * @param surveyId 종료할 설문의 ID
     * @return 업데이트된 설문의 ID
     */
    @Transactional
    public Long endSurvey(Long surveyId) {
        SurveyEntity survey = getSurveyById(surveyId);
        survey.setOngoing(false);
        return surveyRepository.save(survey).getId();
    }

    // == Private Methods ==

    private SurveyEntity getSurveyById(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사를 찾을 수 없습니다: " + surveyId));
    }
}