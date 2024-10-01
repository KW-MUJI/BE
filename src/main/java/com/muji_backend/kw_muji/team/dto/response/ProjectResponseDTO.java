package com.muji_backend.kw_muji.team.dto.response;

import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime deadLineAt;
    private String image;
    private ProjectRole role;
    private boolean isOnGoing;
}
