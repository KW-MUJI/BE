package com.muji_backend.kw_muji.calendar.dto.response;

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
public class CalendarResponseDto {

    private List<ProjectDto> projects;
    private EventGroup events;

    @Data
    @AllArgsConstructor
    public static class ProjectDto {
        private Long projectId;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventGroup {
        private List<UnivEventDto> univEvents;
        private List<UserEventDto> userEvents;
        private List<ProjectEventDto> projectEvents;
    }

    @Data
    @AllArgsConstructor
    public static class UnivEventDto {
        private Long univcalendarId;
        private String title;
        private LocalDate eventDate;
    }

    @Data
    @AllArgsConstructor
    public static class UserEventDto {
        private Long usercalendarId;
        private String title;
        private LocalDateTime eventDate;
    }

    @Data
    @AllArgsConstructor
    public static class ProjectEventDto {
        private Long usercalendarId;
        private Long projectId;
        private String title;
        private LocalDateTime eventDate;
    }
}