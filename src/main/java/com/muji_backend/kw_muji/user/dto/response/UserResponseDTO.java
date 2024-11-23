package com.muji_backend.kw_muji.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private long id;
    private String email;
    private String accessToken;
    private String refreshToken;
}
