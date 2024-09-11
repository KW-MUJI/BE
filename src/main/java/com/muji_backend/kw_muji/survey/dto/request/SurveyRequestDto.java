package com.muji_backend.kw_muji.survey.dto.request;

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
public class SurveyRequestDto {

    private String title;
    private String description;
    private LocalDate endDate;
    private List<QuestionDto> questions;

    @Data
    @AllArgsConstructor
    public static class QuestionDto {
        private String questionText;
        private QuestionType questionType; // "TEXT" 또는 "CHOICE"
        private List<ChoiceDto> choices; // 객관식인 경우 선택 항목 리스트

        @Data
        @AllArgsConstructor
        public static class ChoiceDto {
            private String choiceText;
        }
    }
}