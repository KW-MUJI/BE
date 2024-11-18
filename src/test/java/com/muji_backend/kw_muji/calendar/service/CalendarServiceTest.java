package com.muji_backend.kw_muji.calendar.service;

import com.muji_backend.kw_muji.calendar.repository.ParticipationRepository;
import com.muji_backend.kw_muji.calendar.repository.ProjectRepository;
import com.muji_backend.kw_muji.calendar.repository.UserCalendarRepository;
import com.muji_backend.kw_muji.calendar.repository.UserEventLinkRepository;
import com.muji_backend.kw_muji.common.entity.*;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.common.entity.enums.UserRole;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Import(CalendarService.class)
class CalendarServiceTest {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCalendarRepository userCalendarRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private UserEventLinkRepository userEventLinkRepository;

    private UserEntity user1;
    private UserEntity user2;
    private ProjectEntity project;

    @BeforeEach
    void setUp() {
        // 사용자1, 사용자2 초기화 및 영속화
        user1 = userRepository.save(UserEntity.builder()
                .name("User 1")
                .email("user1@example.com")
                .password("password1")
                .major("Major1")
                .stuNum(19)
                .role(UserRole.USER)
                .build());

        user2 = userRepository.save(UserEntity.builder()
                .name("User 2")
                .email("user2@example.com")
                .password("password2")
                .major("Major2")
                .stuNum(20)
                .role(UserRole.USER)
                .build());

        // 프로젝트 초기화 및 영속화
        project = projectRepository.save(ProjectEntity.builder()
                .name("Test Project")
                .description("Description1")
                .start(true)
                .createdAt(LocalDateTime.now())
                .deadlineAt(LocalDateTime.now().plusDays(10))
                .build());

        // 사용자1과 사용자2의 프로젝트 참여 생성 및 영속화
        participationRepository.save(ParticipationEntity.builder()
                .users(user1)
                .project(project)
                .role(ProjectRole.CREATOR)
                .build());

        participationRepository.save(ParticipationEntity.builder()
                .users(user2)
                .project(project)
                .role(ProjectRole.MEMBER)
                .build());
    }

    @DisplayName("동일 팀 프로젝트의 동일한 이름과 동일한 eventdate인 일정 두개 중 일정1 삭제 시, 일정2가 정상적으로 남는지 확인한다.")
    @Test
    void 동일_팀_프로젝트의_동일한_이름과_동일한_eventDate인_일정_두개_중_일정1_삭제_시_일정2가_남는지_확인() {
        LocalDateTime eventDate = LocalDateTime.now();

        // 프로젝트에 대해 사용자1과 사용자2 각각의 동일 일정 생성
        // 흐름: user1이 project 일정1을 추가함 -> 같은 프로젝트에 참여 중인 user2에게 동일한 일정 공유
        // 또한, 동일한 제목과 동일한 날짜의 일정2를 추가 -> 같은 프로젝트에 참여중인 user2에게 동일한 일정 공유
        // 일정1 삭제 시, 일정2는 제목과 날짜 모두 같더라도 독립적으로 남아 있어야 한다.

        // 프로젝트에 대해 사용자1과 사용자2 각각의 동일 일정 생성
        UserCalendarEntity sharedEvent1 = userCalendarRepository.save(
                UserCalendarEntity.builder()
                        .title("Team Meeting1")
                        .eventDate(eventDate)
                        .build()
        );

        UserCalendarEntity sharedEvent2 = userCalendarRepository.save(
                UserCalendarEntity.builder()
                        .title("Team Meeting2")
                        .eventDate(eventDate)
                        .build()
        );

        userEventLinkRepository.save(UserEventLinkEntity.builder()
                .users(user1)
                .project(project)
                .userCalendar(sharedEvent1)
                .build());

        userEventLinkRepository.save(UserEventLinkEntity.builder()
                .users(user1)
                .project(project)
                .userCalendar(sharedEvent2)
                .build());

        // 삭제는 user2의 일정1 삭제
        calendarService.deleteCalendarEvent(user1, sharedEvent1.getId());

        // 두 사용자의 일정1이 삭제되고 일정2 1개만 남는지 검증
        List<UserEventLinkEntity> remainingLinks = userEventLinkRepository.findAll();

        // sharedEvent2 일정 링크 하나만 있어야 한다
        assertEquals(1, remainingLinks.size(), "유효한 링크가 하나만 남아 있어야 합니다.");
        assertEquals(sharedEvent2, remainingLinks.get(0).getUserCalendar(), "남아 있는 링크는 sharedEvent2와 연결된 것만 있어야 합니다.");

        // sharedEvent1 일정 링크가 삭제되어 sharedEvent1 UserCalendarEntity가 삭제되었는지 확인
        assertFalse(userCalendarRepository.existsById(sharedEvent1.getId()), "UserCalendarEntity가 삭제되어야 합니다.");
    }
}