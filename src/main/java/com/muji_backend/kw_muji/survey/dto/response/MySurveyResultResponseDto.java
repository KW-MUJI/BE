package com.muji_backend.kw_muji.survey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MySurveyResultResponseDto {

    private Long surveyId;
    private String title;
    private String description;

    @JsonProperty("isOngoing")
    private boolean isOngoing;

    private LocalDate createdAt;
    private LocalDate endDate;
    private List<SurveyDetailResponseDto.QuestionDto> questions;  // 질문 정보를 가져옴
    private List<ResponseDto> responses;  // 응답 정보

    @Data
    @Builder
    @AllArgsConstructor
    public static class ResponseDto {
        private Long responseId;
        private List<AnswerDto> answers;

        @Data
        @Builder
        @AllArgsConstructor
        public static class AnswerDto {
            private Long questionId;
            private String questionText;
            private String questionType;
            private String answerText;
        }
    }
}