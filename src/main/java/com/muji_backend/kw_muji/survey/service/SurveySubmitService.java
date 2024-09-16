package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.*;
import com.muji_backend.kw_muji.survey.dto.request.SurveySubmitRequestDto;
import com.muji_backend.kw_muji.survey.repository.ChoiceRepository;
import com.muji_backend.kw_muji.survey.repository.QuestionRepository;
import com.muji_backend.kw_muji.survey.repository.ResponseRepository;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveySubmitService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final ResponseRepository responseRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    /**
     * 설문 응답을 제출하는 서비스 메서드
     *
     * @param surveyId 설문조사의 ID
     * @param requestDto 설문 응답 데이터
     * @return 저장된 응답 ID
     */
    public Long submitSurvey(Long surveyId, SurveySubmitRequestDto requestDto) {
        // 설문과 사용자 정보를 가져옴
        SurveyEntity survey = findSurveyById(surveyId);
        UserEntity user = findUserById(requestDto.getUserId());

        // 응답을 저장
        ResponseEntity response = createAndSaveResponse(survey, user);

        // 질문과 응답을 처리
        handleAnswers(requestDto, response);

        return response.getId();
    }

    // == Private Methods ==

    private SurveyEntity findSurveyById(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사를 찾을 수 없습니다: " + surveyId));
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다: " + userId));
    }

    private ResponseEntity createAndSaveResponse(SurveyEntity survey, UserEntity user) {
        ResponseEntity response = ResponseEntity.builder()
                .survey(survey)
                .users(user)
                .answer(new ArrayList<>())
                .build();
        return responseRepository.save(response);
    }

    private void handleAnswers(SurveySubmitRequestDto requestDto, ResponseEntity response) {
        for (SurveySubmitRequestDto.AnswerDto answerDto : requestDto.getAnswers()) {
            QuestionEntity question = findQuestionById(answerDto.getQuestionId());

            // 주관식 혹은 객관식 응답 처리
            AnswerEntity answer = createAnswer(response, question, answerDto.getAnswerText());

            // 객관식 선택지의 경우 선택지를 설정
            if (answerDto.getChoiceId() != null) {
                ChoiceEntity choice = findChoiceById(answerDto.getChoiceId());
                answer.setChoice(choice);
            }

            response.getAnswer().add(answer);  // 응답에 추가
        }
        responseRepository.save(response);  // 최종 응답 저장
    }

    private QuestionEntity findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 질문을 찾을 수 없습니다: " + questionId));
    }

    private ChoiceEntity findChoiceById(Long choiceId) {
        return choiceRepository.findById(choiceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 선택지를 찾을 수 없습니다: " + choiceId));
    }

    private AnswerEntity createAnswer(ResponseEntity response, QuestionEntity question, String answerText) {
        return AnswerEntity.builder()
                .response(response)
                .question(question)
                .answerText(answerText != null ? answerText : "")
                .build();
    }
}