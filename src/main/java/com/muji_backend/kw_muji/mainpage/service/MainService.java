package com.muji_backend.kw_muji.mainpage.service;

import com.muji_backend.kw_muji.calendar.service.CalendarService;
import com.muji_backend.kw_muji.kwnotice.service.NoticeService;
import com.muji_backend.kw_muji.mainpage.dto.response.MainResponseDto;
import com.muji_backend.kw_muji.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

    private final NoticeService noticeService;
    private final SurveyService surveyService;
    private final CalendarService calendarService;

    public MainResponseDto getMainInfo(String yearMonth) {

    }
}
