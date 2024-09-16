package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.QuestionEntity;
import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.survey.dto.request.SurveyRequestDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyCreateService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    /**
     * 설문 조사를 생성하고 저장하는 메서드
     *
     * @param userId     설문 작성자의 유저 ID
     * @param requestDto 설문 요청 데이터를 담고 있는 DTO (제목, 설명, 질문, 종료일 등)
     */
    public Long createSurvey(Long userId, SurveyRequestDto requestDto) {
        UserEntity user = findUserById(userId);
        SurveyEntity survey = createSurveyEntity(user, requestDto);

        SurveyEntity savedSurvey = surveyRepository.save(survey);

        List<QuestionEntity> questions = mapToQuestionEntities(requestDto, savedSurvey);
        savedSurvey.setQuestion(questions);

        return savedSurvey.getId();
    }

    // == Private Methods ==

    private UserEntity findUserById(Long userId) {

    }

    private SurveyEntity createSurveyEntity(UserEntity user, SurveyRequestDto requestDto) {

    }

    private List<QuestionEntity> mapToQuestionEntities(SurveyRequestDto requestDto, SurveyEntity savedSurvey) {

    }
}