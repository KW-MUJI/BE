package com.muji_backend.kw_muji.team.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotEmpty(message = "제목을 입력해주세요")
    private String name;

    @NotEmpty(message = "내용을 입력하세요")
    private String description;

    @NotNull
    private LocalDate deadlineAt;

    private MultipartFile[] image;
}
