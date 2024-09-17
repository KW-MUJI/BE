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
public class SurveyResponseDto {

    private int currentPage;
    private int totalPages;
    private long totalItems;
    private List<SurveyItemDto> surveys;

    @Data
    @Builder
    @AllArgsConstructor
    public static class SurveyItemDto {
        private Long surveyId;
        private String title;
        private String description;

        @JsonProperty("isOngoing")
        private boolean isOngoing;

        private LocalDate createdAt;
        private LocalDate endDate;
    }
}