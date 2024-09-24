package com.muji_backend.kw_muji.user.dto.request;

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
public class AuthNumRequestDTO {
    @NotEmpty(message = "이메일을 입력해 주세요")
    @Email
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@kw\\.ac\\.kr$", message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요")
    private String authNum;
}
