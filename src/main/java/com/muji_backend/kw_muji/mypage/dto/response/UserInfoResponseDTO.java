package com.muji_backend.kw_muji.mypage.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDTO {
    private String image;

    private String email;

    @NotEmpty(message = "이름을 입력해 주세요")
    private String name;

    @NotNull(message = "학번을 입력해 주세요")
    private int stuNum;

    @NotEmpty(message = "학과를 입력해 주세요")
    private String major;
}
