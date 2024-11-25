package com.muji_backend.kw_muji.team.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListResponseDTO {
    private Long id;
    private String name;
    private boolean start;
    private LocalDate deadlineAt;
    private String image;
    @JsonProperty("isOngoing")
    private boolean isOnGoing;
}
