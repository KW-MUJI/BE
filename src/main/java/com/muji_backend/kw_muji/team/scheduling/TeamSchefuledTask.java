package com.muji_backend.kw_muji.team.scheduling;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamSchefuledTask {
    private final TeamRepository teamRepo;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateTeamStatus() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<ProjectEntity> ongoingProjects = teamRepo.findAllByIsOnGoingTrueAndDeadlineAtLessThan(today);

        ongoingProjects.forEach(project -> {
            project.setOnGoing(false);
        });

        teamRepo.saveAll(ongoingProjects);

        System.out.println("팀 프로젝트 상태 업데이트 작업이 완료되었습니다.");
    }
}
