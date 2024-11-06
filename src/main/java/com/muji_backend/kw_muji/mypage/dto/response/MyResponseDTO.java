package com.muji_backend.kw_muji.mypage.dto.response;

import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.team.dto.response.MyCreatedProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyResponseDTO {
    private List<String> projects; // my 팀플 (name만 반환)
    private List<MyCreatedProject> createdProjects; // my 모집 팀플
    private List<MySurvey> surveys; // my 설문

    @Data
    @Builder
    @AllArgsConstructor
    public static class MyCreatedProject {
        private String name;
        private LocalDateTime deadlineAt;
        private boolean isOnGoing;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class MySurvey {
        private Long surveyId;
        private String title;
        private LocalDate endDate;
        private boolean isOngoing;
    }
}
