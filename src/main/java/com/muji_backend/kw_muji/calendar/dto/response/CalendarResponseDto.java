package com.muji_backend.kw_muji.calendar.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime eventDate;
    }

    @Data
    @AllArgsConstructor
    public static class ProjectEventDto {
        private Long usercalendarId;
        private Long projectId;
        private String name;
        private String title;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime eventDate;
    }
}