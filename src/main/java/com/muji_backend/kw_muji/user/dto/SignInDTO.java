package com.muji_backend.kw_muji.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInDTO {
    @Email
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@kw\\.ac\\.kr$", message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{5,11}$", message = "비밀번호는 대소문자, 숫자, 특수문자를 포함하고 5자에서 11자 사이여야 합니다")
    private String password;

    private String confirmPassword;
}
