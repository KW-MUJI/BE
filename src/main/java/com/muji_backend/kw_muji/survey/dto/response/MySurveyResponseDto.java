package com.muji_backend.kw_muji.survey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class MySurveyResponseDto {
    private Long surveyId;
    private String title;
    private String description;

    @JsonProperty("isOngoing")
    private boolean isOngoing;

    private LocalDate createdAt;
}