package com.muji_backend.kw_muji.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCreatedProjectResponseDTO {
    private String name;
    private List<ApplicantResponseDTO> applicants;
}
