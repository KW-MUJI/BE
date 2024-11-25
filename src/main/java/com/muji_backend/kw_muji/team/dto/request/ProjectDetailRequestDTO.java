package com.muji_backend.kw_muji.team.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailRequestDTO {
    private Long id;
    private String name;
    private String description;
    private String isDeleteImage;
    private LocalDate deadlineAt;
}
