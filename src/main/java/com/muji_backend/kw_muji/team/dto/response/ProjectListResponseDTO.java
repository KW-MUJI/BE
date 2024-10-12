package com.muji_backend.kw_muji.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListResponseDTO {
    private Long id;
    private String name;
    private boolean start;
    private LocalDateTime deadlineAt;
    private String image;
    private boolean isOnGoing;
}
