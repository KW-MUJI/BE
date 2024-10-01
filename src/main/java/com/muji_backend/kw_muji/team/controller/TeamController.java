package com.muji_backend.kw_muji.team.controller;

import com.muji_backend.kw_muji.common.entity.ParticipationEntity;
import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.ProjectRole;
import com.muji_backend.kw_muji.team.dto.request.RegisterRequestDTO;
import com.muji_backend.kw_muji.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> writeProject(@AuthenticationPrincipal UserEntity userInfo, @Valid RegisterRequestDTO dto, @RequestParam(value = "image", required = false) MultipartFile[] file, BindingResult bindingResult) {
        try {
            // 유효성 검사
            teamService.validation(bindingResult, "name");
            teamService.validation(bindingResult, "description");

            final ProjectEntity teamProject = new ProjectEntity();

            if(file != null && file.length > 0 && !file[0].isEmpty())
                teamProject.setImage(teamService.uploadProjectImage(file, dto.getName()));

            teamProject.setName(dto.getName());
            teamProject.setDescription(dto.getDescription());
            teamProject.setDeadlineAt(dto.getDeadlineAt());

            final ParticipationEntity participation = ParticipationEntity.builder()
                    .project(teamProject)
                    .role(ProjectRole.CREATOR)
                    .users(userInfo)
                    .build();

            if (teamProject.getParticipation() == null)
                teamProject.setParticipation(new ArrayList<>());

            teamProject.getParticipation().add(participation);

            teamService.registerProject(teamProject);

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException | IOException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "팀플 모집 글쓰기 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
