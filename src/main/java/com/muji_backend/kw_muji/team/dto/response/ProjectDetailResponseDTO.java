package com.muji_backend.kw_muji.team.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailResponseDTO {
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate deadlineAt;
    private String image;
    private ProjectRole role;
    @JsonProperty("isOngoing")
    private boolean isOnGoing;
    private boolean start;
}
