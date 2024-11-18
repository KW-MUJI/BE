package com.muji_backend.kw_muji.survey.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveySubmitRequestDto {
    private List<AnswerDto> answers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AnswerDto {
        private Long questionId;
        private Long choiceId;  // 객관식 질문
        private String answerText;  // 주관식 질문
    }
}