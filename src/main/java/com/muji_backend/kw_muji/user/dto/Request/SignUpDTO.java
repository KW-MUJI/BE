package com.muji_backend.kw_muji.user.dto.Request;

import jakarta.validation.constraints.Email;
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
public class SignUpDTO {
    @NotEmpty(message = "이름을 입력해 주세요")
    private String name;

    @NotNull(message = "학번을 입력해 주세요")
    private int stuNum;

    @NotEmpty(message = "학과를 입력해 주세요")
    private String major;

    @NotEmpty(message = "이메일을 입력해 주세요")
    @Email
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@kw\\.ac\\.kr$", message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotEmpty(message = "인증번호를 입력해 주세요")
    private String authNum;

    @NotEmpty(message = "비밀번호를 입력해 주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{5,11}$", message = "비밀번호는 대소문자, 숫자, 특수문자를 포함하고 5자에서 11자 사이여야 합니다")
    private String password;

    @NotEmpty(message = "비밀번호 확인을 입력해 주세요")
    private String confirmPassword;

    private String accessToken;

    private String refreshToken;
}
