package com.muji_backend.kw_muji.mainpage.dto.response;

import com.muji_backend.kw_muji.calendar.dto.response.CalendarResponseDto;
import com.muji_backend.kw_muji.survey.dto.response.SurveyResponseDto;
import com.muji_backend.kw_muji.team.dto.response.ProjectListResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MainResponseDto {
    private Notices notices;
    private List<SurveyResponseDto.SurveyItemDto> surveys;
    private CalendarResponseDto.EventGroup events;
    private List<ProjectListResponseDTO> projects;

    @Data
    @AllArgsConstructor
    public static class Notices {
        private List<NoticeItem> all;
        private List<NoticeItem> general;
        private List<NoticeItem> academic;
        private List<NoticeItem> student;
        private List<NoticeItem> scholarship;
    }

    @Data
    @AllArgsConstructor
    public static class NoticeItem {
        private String title;
        private String link;
        private String updatedDate;
    }
}