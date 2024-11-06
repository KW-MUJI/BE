package com.muji_backend.kw_muji.mypage.dto.response;

import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.team.dto.response.MyCreatedProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyResponseDTO {
    private List<MyProjectResponseDTO> projects; // my 팀플
    private List<MyCreatedProjectResponseDTO> createdProjects; // my 모집 팀플
    private List<MySurveyResponseDto> surveys; // my 설문
}
