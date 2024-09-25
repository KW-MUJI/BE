package com.muji_backend.kw_muji.calendar.service;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UnivCalendarRepository univCalendarRepository;
    private final UserCalendarRepository userCalendarRepository;
    private final ParticipationRepository participationRepository;

    public CalendarResponseDto getCalendarEvents(UserEntity userInfo, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth); // yyyy-MM 형식
        LocalDateTime startDateTime = ym.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = ym.atEndOfMonth().atTime(23, 59, 59);

        // 대학 일정 조회
        List<UnivCalendarEntity> univEvents = univCalendarRepository.findAllByUsersAndEventDateBetween(userInfo, startDateTime, endDateTime);

        // 개인 일정 조회 (projectId가 null인 경우)
        List<UserCalendarEntity> userEvents = userCalendarRepository.findAllByUsersAndProjectIsNullAndEventDateBetween(userInfo, startDateTime, endDateTime);

        // 참여중인 프로젝트 조회 (Role이 CREATOR나 MEMBER인 경우, start 값이 true인 프로젝트)
        List<ParticipationEntity> participations = participationRepository.findAllByUsersAndRoleInAndProjectStartTrue(userInfo, List.of(ProjectRole.CREATOR, ProjectRole.MEMBER));
        List<ProjectEntity> projects = participations.stream()
                .map(ParticipationEntity::getProject)
                .toList();

        // 참여중인 프로젝트 일정 조회
        List<UserCalendarEntity> projectEvents = projects.stream()
                .flatMap(project -> userCalendarRepository.findAllByProjectAndEventDateBetween(project, startDateTime, endDateTime).stream())
                .toList();

    }
}
