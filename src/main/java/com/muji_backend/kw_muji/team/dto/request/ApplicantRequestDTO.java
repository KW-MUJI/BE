package com.muji_backend.kw_muji.team.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantRequestDTO {
    @NotEmpty(message = "지원자 아이디가 비어있습니다.")
    private Long id;
}
