package com.muji_backend.kw_muji.survey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muji_backend.kw_muji.common.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDetailResponseDto {

    private Long surveyId;
    private String title;
    private String description;

    @JsonProperty("isOngoing")
    private boolean isOngoing;

    private LocalDate createdAt;
    private LocalDate endDate;
    private List<QuestionDto> questions;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionDto {
        private Long questionId;
        private String questionText;
        private QuestionType questionType;
        private List<ChoiceDto> choices;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ChoiceDto {
            private Long choiceId;
            private String choiceText;
        }
    }
}