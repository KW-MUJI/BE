package com.muji_backend.kw_muji.survey.service;

import com.muji_backend.kw_muji.common.entity.ChoiceEntity;
import com.muji_backend.kw_muji.common.entity.QuestionEntity;
import com.muji_backend.kw_muji.common.entity.SurveyEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.QuestionType;
import com.muji_backend.kw_muji.survey.dto.request.SurveyRequestDto;
import com.muji_backend.kw_muji.survey.repository.SurveyRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다." + userId));
    }

    private SurveyEntity createSurveyEntity(UserEntity user, SurveyRequestDto requestDto) {
        LocalDate endDate = requestDto.getEndDate();
        boolean isOngoing = !endDate.isBefore(LocalDate.now());

        return SurveyEntity.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .endDate(requestDto.getEndDate())
                .isOngoing(isOngoing)
                .users(user)
                .build();
    }

    private List<QuestionEntity> mapToQuestionEntities(SurveyRequestDto requestDto, SurveyEntity savedSurvey) {
        return requestDto.getQuestions().stream()
                .map(questionDto -> {
                    QuestionEntity question = QuestionEntity.builder()
                            .questionText(questionDto.getQuestionText())
                            .questionType(questionDto.getQuestionType())
                            .survey(savedSurvey)
                            .build();

                    if (questionDto.getQuestionType() == QuestionType.CHOICE && questionDto.getChoices() != null) {
                        List<ChoiceEntity> choices = questionDto.getChoices().stream()
                                .map(choiceDto -> ChoiceEntity.builder()
                                        .choiceText(choiceDto.getChoiceText())
                                        .question(question)
                                        .build())
                                .collect(Collectors.toList());
                        question.setChoice(choices); // 객관식 질문일 경우에만 choices를 설정
                    }

                    return question;
                }).collect(Collectors.toList());
    }
}