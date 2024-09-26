package com.muji_backend.kw_muji.calendar.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CalendarRequestDto {
    private Long projectId;           // 프로젝트 ID (null이면 개인 일정)
    private String title;
    private LocalDateTime eventDate;
}