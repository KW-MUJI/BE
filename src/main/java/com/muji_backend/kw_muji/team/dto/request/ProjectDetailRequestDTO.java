package com.muji_backend.kw_muji.team.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailRequestDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private boolean isDeleteImage;
}
