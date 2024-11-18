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
public class ResumeResponseDTO {
    private long id;
    private String name;
    private LocalDateTime createdAt;
}
