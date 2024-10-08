package com.muji_backend.kw_muji.mainpage.service;

import com.muji_backend.kw_muji.calendar.dto.response.CalendarResponseDto;
import com.muji_backend.kw_muji.calendar.service.CalendarService;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.service.NoticeService;
import com.muji_backend.kw_muji.mainpage.dto.response.MainResponseDto;
import com.muji_backend.kw_muji.survey.dto.response.SurveyResponseDto;
import com.muji_backend.kw_muji.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {

    private final NoticeService noticeService;
    private final SurveyService surveyService;
    private final CalendarService calendarService;

    public MainResponseDto getMainInfo(UserEntity userInfo, String yearMonth) {

        // 공지사항 불러오기 및 매핑 (각 카테고리별 상위 6개씩)
        MainResponseDto.Notices notices = new MainResponseDto.Notices(
                mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "").getNotices()),
                mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "0").getNotices()),
                mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "1").getNotices()),
                mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "2").getNotices()),
                mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "4").getNotices())
        );

        // 설문조사 불러오기 (최대 4개)
        List<SurveyResponseDto.SurveyItemDto> surveys = (surveyService.getSurveys("", 0) != null)
                ? surveyService.getSurveys("", 0).getSurveys().stream()
                .limit(4)
                .collect(Collectors.toList())
                : List.of();

        // 캘린더 이벤트 불러오기 - 캘린더 이벤트는 로그인한 사용자만 조회 가능
        CalendarResponseDto.EventGroup events = null;
        if (userInfo != null) {
            CalendarResponseDto calendarResponseDto = calendarService.getCalendarEvents(userInfo, yearMonth);
            events = (calendarResponseDto != null) ? calendarResponseDto.getEvents() : new CalendarResponseDto.EventGroup();
        }

        return MainResponseDto.builder()
                .notices(notices)
                .surveys(surveys)
                .events(events)
                .build();
    }

    // == Private Methods ==

    // NoticeResponse.Notice 리스트를 MainResponseDto.NoticeItem 리스트로 변환하는 매핑 함수
    private List<MainResponseDto.NoticeItem> mapToNoticeItems(List<NoticeResponse.Notice> notices) {
        return notices.stream()
                .limit(6)
                .map(notice -> new MainResponseDto.NoticeItem(
                        notice.getTitle(),
                        notice.getLink(),
                        notice.getUpdatedDate()
                ))
                .collect(Collectors.toList());
    }
}
