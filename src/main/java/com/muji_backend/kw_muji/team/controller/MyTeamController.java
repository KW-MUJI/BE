package com.muji_backend.kw_muji.team.controller;

import com.muji_backend.kw_muji.common.entity.ProjectEntity;
import com.muji_backend.kw_muji.team.dto.request.ProjectDetailRequestDTO;
import com.muji_backend.kw_muji.team.dto.request.ProjectStartRequestDTO;
import com.muji_backend.kw_muji.team.dto.response.MyCreatedProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import com.muji_backend.kw_muji.team.dto.response.ProjectDetailResponseDTO;
import com.muji_backend.kw_muji.team.service.MyTeamService;
import com.muji_backend.kw_muji.team.service.TeamService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/myteam")
public class MyTeamController {
    private final MyTeamService myTeamService;
    private final TeamService teamService;

    @Value("${cloud.aws.s3.url}")
    private String bucketURL;

    @GetMapping("/participation")
    public ResponseEntity<Map<String, Object>> getMyProjects(@AuthenticationPrincipal UserEntity userInfo) {
        try {
            final List<MyProjectResponseDTO> projects = myTeamService.getMyProjects(userInfo);

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", projects));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "MY 팀플 로딩 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @GetMapping("/applicant")
    public ResponseEntity<Map<String, Object>> getApplicants(@AuthenticationPrincipal UserEntity userInfo) {
        try {
            final List<MyCreatedProjectResponseDTO> projects = myTeamService.getMyCreatedProjects(userInfo);

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", projects));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "MY 모집 팀플 로딩 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<Map<String, Object>> deleteProject(@AuthenticationPrincipal UserEntity userInfo, @PathVariable Long projectId) {
        try {
            myTeamService.deleteProject(projectId, userInfo);

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "팀 프로젝트 삭제 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @GetMapping("/update/{projectId}")
    public ResponseEntity<Map<String, Object>> getUpdateProject(@AuthenticationPrincipal UserEntity userInfo, @PathVariable Long projectId) {
        try {
            if (!myTeamService.isMyProject(projectId, userInfo))
                throw new IllegalArgumentException("권한이 없습니다.");

            final ProjectEntity project = teamService.getProject(projectId);

            final ProjectDetailResponseDTO resDTO = ProjectDetailResponseDTO.builder()
                    .name(project.getName())
                    .description(project.getDescription())
                    .createdAt(project.getCreatedAt())
                    .deadlineAt(LocalDate.from(project.getDeadlineAt()))
                    .image(project.getImage() != null ? bucketURL + URLEncoder.encode(project.getImage(), "UTF-8") : "")
                    .role(teamService.getRole(projectId, userInfo) == null ? null : teamService.getRole(projectId, userInfo).getRole())
                    .isOnGoing(project.isOnGoing())
                    .build();

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", resDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "팀 프로젝트 정보 불러오기 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProject(@AuthenticationPrincipal UserEntity userInfo,
                                                             ProjectDetailRequestDTO dto,
                                                             @RequestParam(value = "projectImage", required = false) MultipartFile[] file) {
        try {
            if (!myTeamService.isMyProject(dto.getId(), userInfo))
                throw new IllegalArgumentException("권한이 없습니다.");

            final ProjectEntity project = teamService.getProject(dto.getId());

            final boolean isDeleteImage = Boolean.parseBoolean(dto.getIsDeleteImage());

            if (file != null && file.length > 0 && !file[0].isEmpty())
                project.setImage(myTeamService.uploadProjectImage(file, dto.getName()));
            else if (isDeleteImage) // 팀 프로젝트 사진 삭제를 요청한 경우
                myTeamService.deleteProjectImage(userInfo.getId());

            myTeamService.updateProject(project, dto);

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "팀 프로젝트 수정 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PatchMapping("/start")
    public ResponseEntity<Map<String, Object>> startProject(@AuthenticationPrincipal UserEntity userInfo, @RequestBody ProjectStartRequestDTO dto) {
        try {
            if(!myTeamService.isMyProject(dto.getProjectId(), userInfo))
                throw new IllegalArgumentException("내가 생성한 프로젝트가 아님");

            myTeamService.updateStart(dto);

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "MY 팀플 로딩 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
