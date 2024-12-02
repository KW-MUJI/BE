package com.muji_backend.kw_muji.mainpage.service;

import com.muji_backend.kw_muji.calendar.dto.response.CalendarResponseDto;
import com.muji_backend.kw_muji.calendar.service.CalendarService;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.service.NoticeService;
import com.muji_backend.kw_muji.mainpage.dto.response.MainResponseDto;
import com.muji_backend.kw_muji.survey.dto.response.SurveyResponseDto;
import com.muji_backend.kw_muji.survey.service.SurveyService;
import com.muji_backend.kw_muji.team.dto.response.ProjectListResponseDTO;
import com.muji_backend.kw_muji.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {

    private final NoticeService noticeService;
    private final SurveyService surveyService;
    private final CalendarService calendarService;
    private final TeamService teamService;

    public MainResponseDto getMainInfo(UserEntity userInfo, String yearMonth) {

        try {
            // 공지사항 비동기 호출
            CompletableFuture<List<MainResponseDto.NoticeItem>> generalNotices = CompletableFuture.supplyAsync(() ->
                    mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "").getNotices()));
            CompletableFuture<List<MainResponseDto.NoticeItem>> category0Notices = CompletableFuture.supplyAsync(() ->
                    mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "0").getNotices()));
            CompletableFuture<List<MainResponseDto.NoticeItem>> category1Notices = CompletableFuture.supplyAsync(() ->
                    mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "1").getNotices()));
            CompletableFuture<List<MainResponseDto.NoticeItem>> category2Notices = CompletableFuture.supplyAsync(() ->
                    mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "2").getNotices()));
            CompletableFuture<List<MainResponseDto.NoticeItem>> category4Notices = CompletableFuture.supplyAsync(() ->
                    mapToNoticeItems(noticeService.getKwHomeNotices(1, "", "4").getNotices()));

            // 모든 공지사항 작업 완료 대기
            CompletableFuture.allOf(
                    generalNotices, category0Notices, category1Notices,
                    category2Notices, category4Notices
            ).join();

            // 공지사항 결과 병합
            MainResponseDto.Notices notices = new MainResponseDto.Notices(
                    generalNotices.get(),
                    category0Notices.get(),
                    category1Notices.get(),
                    category2Notices.get(),
                    category4Notices.get()
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

            // 팀플 모집 (최대 4개)
            Map<String, Object> result = teamService.getOnGoingProjects(0, "");
            List<ProjectListResponseDTO> projects = result.get("projects") != null
                    ? ((List<ProjectListResponseDTO>) result.get("projects")).stream()
                    .limit(4)
                    .toList()
                    : List.of();

            return MainResponseDto.builder()
                    .notices(notices)
                    .surveys(surveys)
                    .events(events)
                    .projects(projects)
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("공지사항 병렬 처리 중 오류 발생", e);
        }
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
