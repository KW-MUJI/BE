package com.muji_backend.kw_muji.calendar.service;

import com.muji_backend.kw_muji.calendar.dto.request.CalendarRequestDto;
import com.muji_backend.kw_muji.calendar.dto.response.CalendarResponseDto;
import com.muji_backend.kw_muji.calendar.repository.*;
import com.muji_backend.kw_muji.common.entity.*;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
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
    private final UserEventLinkRepository userEventLinkRepository;

    /**
     * 참여 팀플, 대학, 개인, 그리고 참여 중인 프로젝트 일정을 조회하는 메서드
     *
     * @param userInfo  현재 인증된 사용자 정보
     * @param yearMonth 조회할 연도-월 (yyyy-MM 형식)
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
        List<UserCalendarEntity> userEvents = safeList(() -> userEventLinkRepository.findAllByUsersAndProjectIsNull(userInfo).stream()
                .map(UserEventLinkEntity::getUserCalendar)
                .filter(calendar -> calendar.getEventDate().isAfter(startDateTime) && calendar.getEventDate().isBefore(endDateTime)) // 날짜 범위에 있는 일정만 필터링
                .sorted(Comparator.comparing(UserCalendarEntity::getEventDate)) // 날짜 오름차순
                .toList()
        );


        // 참여중인 프로젝트 조회 (Role이 CREATOR나 MEMBER인 경우, start 값이 true인 프로젝트)
        List<ProjectEntity> projects = participationRepository.findAllByUsersAndRoleInAndProjectStartTrue(userInfo, List.of(ProjectRole.CREATOR, ProjectRole.MEMBER))
                .stream()
                .map(ParticipationEntity::getProject)
                .toList();

        // 참여중인 프로젝트 일정 조회
        List<CalendarResponseDto.ProjectEventDto> projectEvents = safeList(() -> projects.stream()
                .flatMap(project -> userEventLinkRepository.findAllByProject(project).stream())
                .map(link -> {
                    UserCalendarEntity calendar = link.getUserCalendar();
                    ProjectEntity project = link.getProject();
                    return new CalendarResponseDto.ProjectEventDto(
                            calendar.getId(),
                            project.getId(),
                            project.getName(),
                            calendar.getTitle(),
                            calendar.getEventDate()
                    );
                })
                .filter(dto -> dto.getEventDate().isAfter(startDateTime) && dto.getEventDate().isBefore(endDateTime)) // 날짜 범위에 있는 일정만 필터링
                .sorted(Comparator.comparing(CalendarResponseDto.ProjectEventDto::getEventDate)) // 날짜 오름차순
                .toList()
        );

        return CalendarResponseDto.builder()
                .projects(projects.stream()
                        .map(p -> new CalendarResponseDto.ProjectDto(p.getId(), p.getName()))
                        .collect(Collectors.toList()))
                .events(new CalendarResponseDto.EventGroup(
                        univEvents.stream()
                                .map(e -> new CalendarResponseDto.UnivEventDto(e.getId(), e.getTitle(), e.getEventDate().toLocalDate()))
                                .collect(Collectors.toList()),
                        userEvents.stream()
                                .map(e -> new CalendarResponseDto.UserEventDto(e.getId(), e.getTitle(), e.getEventDate()))
                                .collect(Collectors.toList()),
                        projectEvents
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
    @Transactional
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

        // UserCalendarEntity 생성 및 저장
        UserCalendarEntity calendarEntity = UserCalendarEntity.builder()
                .title(requestDto.getTitle())
                .eventDate(requestDto.getEventDate())
                .build();
        userCalendarRepository.save(calendarEntity);

        Long userCalendarId = calendarEntity.getId();

        if (project == null) { // 개인 일정인 경우
            addUserEventLink(userInfo, null, calendarEntity);
        }
        else if(project.isStart()){ // 프로젝트 일정인 경우
            // 프로젝트와 연결된 모든 CREATOR 또는 MEMBER 참여자에게 동일한 일정 연결
            List<ParticipationEntity> participats = participationRepository.findAllByProjectAndRoleIn(project, List.of(ProjectRole.CREATOR, ProjectRole.MEMBER));

            for (ParticipationEntity participant : participats) {
                addUserEventLink(participant.getUsers(), project, calendarEntity);
            }
        }
        else {
            throw new IllegalStateException("아직 시작되지 않은 팀플입니다. projectId: " + project.getId());
        }

        return userCalendarId;
    }

    // == Private Methods ==

    // UserEventLinkEntity를 생성하여 UserCalendarEntity와 연결
    private void addUserEventLink(UserEntity user, ProjectEntity project, UserCalendarEntity calendarEntity) {
        UserEventLinkEntity eventLinkEntity = UserEventLinkEntity.builder()
                .users(user)
                .project(project)
                .userCalendar(calendarEntity)
                .build();
        userEventLinkRepository.save(eventLinkEntity);
    }

    /**
     * 개인 또는 팀플 일정을 삭제하는 메서드
     *
     * @param userInfo       현재 인증된 사용자 정보
     * @param usercalendarId 삭제할 일정 ID
     */
    @Transactional
    public void deleteCalendarEvent(UserEntity userInfo, Long usercalendarId) {
        if (userInfo == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }

        // 삭제할 일정의 링크 조회
        UserEventLinkEntity userEventLink = userEventLinkRepository.findByUsersAndUserCalendarId(userInfo, usercalendarId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정을 찾을 수 없습니다. usercalendarId: " + usercalendarId));

        // cascade = CascadeType.ALL, orphanRemoval = true 설정에 의해서
        // UserCalendarEntity 삭제 - 관련된 모든 UserEventLinkEntity도 자동으로 삭제됨
        userCalendarRepository.delete(userEventLink.getUserCalendar());
    }
}
