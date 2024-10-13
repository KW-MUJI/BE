package com.muji_backend.kw_muji.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {
    private String image;
    private String name;
    private int stuNum;
    private String major;
    private String email;
}
