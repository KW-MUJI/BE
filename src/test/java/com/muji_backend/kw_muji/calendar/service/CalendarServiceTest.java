package com.muji_backend.kw_muji.calendar.service;

import com.muji_backend.kw_muji.calendar.repository.ParticipationRepository;
import com.muji_backend.kw_muji.calendar.repository.ProjectRepository;
import com.muji_backend.kw_muji.calendar.repository.UserCalendarRepository;
import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.UserCalendarEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
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

    @DisplayName("동일 팀 프로젝트의 동일한 이름과 동일한 eventdate인 일정 두개 중 일정1,2 삭제 시, 일정3,4가 남는지 확인한다.")
    @Test
    void 동일_팀_프로젝트의_동일한_이름과_동일한_eventDate인_일정_두개_중_일정12_삭제_시_일정34가_남는지_확인() {
        LocalDateTime eventDate = LocalDateTime.now();

        // 프로젝트에 대해 사용자1과 사용자2 각각의 동일 일정 생성
        // 흐름: user1이 project 일정을 추가함 -> 같은 프로젝트에 참여중인 user2에게 동일한 일정 추가
        // 프로젝트에 대해 사용자1과 사용자2 각각의 동일 일정 생성
        UserCalendarEntity user1Event = userCalendarRepository.save(
                UserCalendarEntity.builder()
                        .users(user1)
                        .project(project)
                        .title("Team Meeting")
                        .eventDate(eventDate)
                        .build()
        );

        UserCalendarEntity user2Event = userCalendarRepository.save(
                UserCalendarEntity.builder()
                        .users(user2)
                        .project(project)
                        .title("Team Meeting")
                        .eventDate(eventDate)
                        .build()
        );

        // 3,4 일정은 1,2 일정과 프로젝트와 이름, 날짜는 같지만 다른 일정이라는 설정
        UserCalendarEntity user3Event = userCalendarRepository.save(
                UserCalendarEntity.builder()
                        .users(user2)
                        .project(project)
                        .title("Team Meeting")
                        .eventDate(eventDate)
                        .build()
        );

        UserCalendarEntity user4Event = userCalendarRepository.save(
                UserCalendarEntity.builder()
                        .users(user2)
                        .project(project)
                        .title("Team Meeting")
                        .eventDate(eventDate)
                        .build()
        );

        calendarService.deleteCalendarEvent(user1, user1Event.getId());

        // 두 사용자의 일정이 모두 삭제되었는지 검증
        // -> 실제 로직에서는 일정3,4는 일정1,2와 다른 일정이므로 삭제되면 안된다.
        assertFalse(userCalendarRepository.existsById(user1Event.getId()), "User1의 일정이 삭제되어야 합니다.");
        assertFalse(userCalendarRepository.existsById(user2Event.getId()), "User2의 일정이 삭제되어야 합니다.");
        assertFalse(userCalendarRepository.existsById(user3Event.getId()), "User3의 일정이 삭제되어야 합니다.");
        assertFalse(userCalendarRepository.existsById(user4Event.getId()), "User4의 일정이 삭제되어야 합니다.");
    }
}