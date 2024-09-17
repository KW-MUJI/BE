package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        List<SurveyEntity> userSurveys = surveyRepository.findByUsers(user);

        return userSurveys.stream()
                .map(this::mapToMySurveyResponseDto)
                .collect(Collectors.toList());
    }

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
}