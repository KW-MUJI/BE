package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.AnswerEntity;
import com.muji_backend.kw_muji.common.entity.ResponseEntity;
import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.survey.dto.response.MySurveyResultResponseDto;
import com.muji_backend.kw_muji.survey.dto.response.SurveyDetailResponseDto;
import com.muji_backend.kw_muji.survey.repository.ResponseRepository;
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
    private final ResponseRepository responseRepository;

    private final SurveyService surveyService;

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

    /**
     * 설문조사를 삭제하는 메서드
     *
     * @param surveyId 삭제할 설문의 ID
     */
    @Transactional
    public void deleteSurvey(Long surveyId) {
        SurveyEntity survey = getSurveyById(surveyId);
        surveyRepository.delete(survey);
    }

    /**
     * 설문조사 결과를 조회하는 메서드
     *
     * @param surveyId 조회할 설문의 ID
     * @return 설문조사와 응답 결과
     */
    public MySurveyResultResponseDto getSurveyResult(Long surveyId) {
        // 설문조사 기본 정보 및 질문을 SurveyService에서 가져옴
        SurveyDetailResponseDto surveyDetail = surveyService.getSurveyDetail(surveyId);

        // 설문조사에 대한 응답 데이터 가져오기
        List<ResponseEntity> responses = responseRepository.findBySurveyId(surveyId);
        List<MySurveyResultResponseDto.ResponseDto> responseDtos = mapToResponseDtos(responses);

        return MySurveyResultResponseDto.builder()
                .surveyId(surveyDetail.getSurveyId())
                .title(surveyDetail.getTitle())
                .description(surveyDetail.getDescription())
                .isOngoing(surveyDetail.isOngoing())
                .createdAt(surveyDetail.getCreatedAt())
                .endDate(surveyDetail.getEndDate())
                .questions(surveyDetail.getQuestions())  // 설문조사 기본 정보의 질문 포함
                .responses(responseDtos)  // 응답 데이터
                .build();
    }

    // == Private Methods ==

    private List<MySurveyResultResponseDto.ResponseDto> mapToResponseDtos(List<ResponseEntity> responses) {
        return responses.stream()
                .map(response -> MySurveyResultResponseDto.ResponseDto.builder()
                        .responseId(response.getId())
                        .answers(mapToAnswerDtos(response.getAnswer()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<MySurveyResultResponseDto.ResponseDto.AnswerDto> mapToAnswerDtos(List<AnswerEntity> answers) {
        return answers.stream()
                .map(answer -> MySurveyResultResponseDto.ResponseDto.AnswerDto.builder()
                        .questionId(answer.getQuestion().getId())
                        .questionText(answer.getQuestion().getQuestionText())
                        .questionType(answer.getQuestion().getQuestionType().name())
                        .answerText(getAnswerText(answer))
                        .build())
                .collect(Collectors.toList());
    }

    private String getAnswerText(AnswerEntity answer) {
        return (answer.getChoice() != null) ? answer.getChoice().getChoiceText() : answer.getAnswerText();
    }
}