package com.muji_backend.kw_muji.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApplicantResponseDTO {
    private String image;
    private String name;
    private int stuNum;
    private String major;
    private String resume;
}
