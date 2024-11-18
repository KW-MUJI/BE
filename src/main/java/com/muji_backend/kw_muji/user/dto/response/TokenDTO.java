package com.muji_backend.kw_muji.user.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    @NotEmpty(message = "액세스 토큰이 비어 있어요")
    private String accessToken;

    @NotEmpty(message = "리프레시 토큰이 비어 있어요")
    private String refreshToken;
}
