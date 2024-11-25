package com.muji_backend.kw_muji.mypage.dto.request;

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
public class UpdateRequestDTO {
    @NotNull
    private String isDeleteImage;

    @NotEmpty(message = "이름을 입력해 주세요")
    private String name;

    @NotNull(message = "학번을 입력해 주세요")
    private int stuNum;

    @NotEmpty(message = "학과를 입력해 주세요")
    private String major;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{5,11}$", message = "비밀번호는 대소문자, 숫자, 특수문자를 포함하고 5자에서 11자 사이여야 합니다")
    private String password;

    private String confirmPassword;
}
