package com.muji_backend.kw_muji.calendar.service;

import com.muji_backend.kw_muji.calendar.dto.request.CalendarRequestDto;
import com.muji_backend.kw_muji.calendar.dto.response.CalendarResponseDto;
import com.muji_backend.kw_muji.calendar.repository.ParticipationRepository;
import com.muji_backend.kw_muji.calendar.repository.UnivCalendarRepository;
import com.muji_backend.kw_muji.calendar.repository.UserCalendarRepository;
import com.muji_backend.kw_muji.common.entity.*;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UnivCalendarRepository univCalendarRepository;
    private final UserCalendarRepository userCalendarRepository;
    private final ParticipationRepository participationRepository;

    public CalendarResponseDto getCalendarEvents(UserEntity userInfo, String yearMonth) {
        if (userInfo == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }

        YearMonth ym;
        try {
            ym = YearMonth.parse(yearMonth); // yyyy-MM 형식
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. yyyy-MM 형식을 사용해주세요.");
        }

        LocalDateTime startDateTime = ym.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = ym.atEndOfMonth().atTime(23, 59, 59);

        // 대학 일정 조회
        List<UnivCalendarEntity> univEvents = safeList(() -> univCalendarRepository.findAllByUsersAndEventDateBetween(userInfo, startDateTime, endDateTime));

        // 개인 일정 조회 (projectId가 null인 경우)
        List<UserCalendarEntity> userEvents = safeList(() -> userCalendarRepository.findAllByUsersAndProjectIsNullAndEventDateBetween(userInfo, startDateTime, endDateTime));

        // 참여중인 프로젝트 조회 (Role이 CREATOR나 MEMBER인 경우, start 값이 true인 프로젝트)
        List<ProjectEntity> projects = participationRepository.findAllByUsersAndRoleInAndProjectStartTrue(userInfo, List.of(ProjectRole.CREATOR, ProjectRole.MEMBER))
                .stream()
                .map(ParticipationEntity::getProject)
                .toList();

        // 참여중인 프로젝트 일정 조회
        List<UserCalendarEntity> projectEvents = safeList(() -> userCalendarRepository.findAllByProjectInAndEventDateBetween(projects, startDateTime, endDateTime));

        return CalendarResponseDto.builder()
                .projects(projects.stream()
                        .map(p -> new CalendarResponseDto.ProjectDto(p.getId(),p.getName()))
                        .collect(Collectors.toList()))
                .events(new CalendarResponseDto.EventGroup(
                        univEvents.stream()
                                .map(e -> new CalendarResponseDto.UnivEventDto(e.getId(), e.getTitle(), e.getEventDate().toLocalDate()))
                                .collect(Collectors.toList()),
                        userEvents.stream()
                                .map(e -> new CalendarResponseDto.UserEventDto(e.getId(), e.getTitle(), e.getEventDate()))
                                .collect(Collectors.toList()),
                        projectEvents.stream()
                                .map(e -> new CalendarResponseDto.ProjectEventDto(e.getId(), e.getProject().getId(), e.getTitle(), e.getEventDate()))
                                .collect(Collectors.toList())
                ))
                .build();
    }

    // == Private Methods ==

    // 결과가 null인 경우 빈 리스트 반환
    private <T> List<T> safeList(Supplier<List<T>> queryFunction) {
        List<T> result = queryFunction.get();
        return result != null ? result : Collections.emptyList();
    }

    public Long addCalendarEvent(UserEntity userInfo, CalendarRequestDto requestDto) {

    }
}
