package com.muji_backend.kw_muji.calendar.service;

import com.muji_backend.kw_muji.calendar.dto.request.CalendarRequestDto;
import com.muji_backend.kw_muji.calendar.dto.response.CalendarResponseDto;
import com.muji_backend.kw_muji.calendar.repository.ParticipationRepository;
import com.muji_backend.kw_muji.calendar.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;

    /**
     * 참여 팀플, 대학, 개인, 그리고 참여 중인 프로젝트 일정을 조회하는 메서드
     *
     * @param userInfo   현재 인증된 사용자 정보
     * @param yearMonth  조회할 연도-월 (yyyy-MM 형식)
     * @return CalendarResponseDto 참여중인 팀플, 대학 일정, 개인 일정, 프로젝트 일정 데이터를 포함한 응답 DTO
     */
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
                                .map(e -> new CalendarResponseDto.ProjectEventDto(e.getId(), e.getProject().getId(), e.getProject().getName(), e.getTitle(), e.getEventDate()))
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

    /**
     * 개인 일정 또는 팀플 일정을 생성하는 메서드
     * 프로젝트 ID가 null인 경우 개인 일정으로 처리
     * 프로젝트 ID가 있을 경우 팀플에 속한 팀원들에게도 동일한 일정이 추가
     * 팀원의 Role이 CREATOR나 MEMBER이고, 프로젝트의 start 값이 true인 경우에만 일정이 추가됨
     *
     * @param userInfo   현재 인증된 사용자 정보
     * @param requestDto 일정 생성에 필요한 데이터 (제목, 날짜, 프로젝트 ID 등)
     * @return 생성된 일정의 ID
     */
    public Long addCalendarEvent(UserEntity userInfo, CalendarRequestDto requestDto) {
        if (userInfo == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }

        // 프로젝트가 있는지 확인 (null이면 개인 일정)
        ProjectEntity project = null;
        if (requestDto.getProjectId() != null) {
            project = projectRepository.findById(requestDto.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 조회할 수 없습니다. projectId: " + requestDto.getProjectId()));
        }

        // 팀플 일정인 경우, 팀원의 Role이 CREATOR나 MEMBER이며 프로젝트가 시작된 경우에만 일정 추가
        if (project != null && project.isStart()) {
            List<ParticipationEntity> participants = participationRepository.findAllByProjectAndRoleIn(project, List.of(ProjectRole.CREATOR, ProjectRole.MEMBER));

            // 팀원 각각에게 일정 추가
            for (ParticipationEntity participant : participants) {
                addUserCalendarEvent(participant.getUsers(), project, requestDto);
            }
        } else if (project != null && !project.isStart()){
            throw new IllegalStateException("아직 시작되지 않은 팀플입니다. projectId: " + project.getId());
        } else {
            // 개인 일정일 경우 본인에게만 일정 추가
            return addUserCalendarEvent(userInfo, null, requestDto);
        }

        return null;
    }

    // == Private Methods ==

    // 개인 일정 또는 팀플 일정을 특정 사용자에게 추가하는 메서드
    private Long addUserCalendarEvent(UserEntity userInfo, ProjectEntity project, CalendarRequestDto requestDto) {
        UserCalendarEntity calendarEntity = UserCalendarEntity.builder()
                .users(userInfo)
                .project(project)
                .title(requestDto.getTitle())
                .eventDate(requestDto.getEventDate())
                .build();

        UserCalendarEntity savedEvent = userCalendarRepository.save(calendarEntity);
        return savedEvent.getId();
    }

    /**
     * 개인 일정을 삭제하는 메서드
     *
     * @param userInfo       현재 인증된 사용자 정보
     * @param usercalendarId 삭제할 일정 ID
     */
    public void deleteCalendarEvent(UserEntity userInfo, Long usercalendarId) {
        if (userInfo == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }

        // 삭제할 일정 조회
        UserCalendarEntity calendarEntity = userCalendarRepository.findById(usercalendarId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정을 찾을 수 없습니다. usercalendarId: " + usercalendarId));

        // 일정 소유자 확인
        if (!calendarEntity.getUsers().getId().equals(userInfo.getId())) {
            throw new IllegalArgumentException("본인의 일정만 삭제할 수 있습니다.");
        }

        // 일정 삭제
        userCalendarRepository.delete(calendarEntity);
    }
}
