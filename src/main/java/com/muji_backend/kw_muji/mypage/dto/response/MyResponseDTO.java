package com.muji_backend.kw_muji.mypage.dto.response;

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
    private MyProfile profile; // 내 정보
    private List<String> projects; // my 팀플 (name만 반환)
    private List<MyCreatedProject> createdProjects; // my 모집 팀플
    private List<MySurvey> surveys; // my 설문
    private List<Resume> resumes; // 포트폴리오
    private List<applicationProject> applicationProjects; // 지원한 팀플

    @Data
    @Builder
    @AllArgsConstructor
    public static class MyProfile {
        private Long userId;
        private String userImage;
        private String username;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class MyCreatedProject {
        private String name;
        private LocalDateTime deadlineAt;
        private boolean isOngoing;
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

    @Data
    @Builder
    @AllArgsConstructor
    public static class Resume {
        private Long resumeId;
        private String name;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class applicationProject {
        private String name;
        private int applicantsNum;
        private LocalDateTime deadlineAt;
    }
}
