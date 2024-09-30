package com.muji_backend.kw_muji.project.controller;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.project.dto.request.RegisterRequestDTO;
import com.muji_backend.kw_muji.project.service.ProjectService;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/register")
    public org.springframework.http.ResponseEntity<Map<String, Object>> writeProject(@AuthenticationPrincipal UserEntity userInfo, RegisterRequestDTO dto, @RequestParam(value = "image", required = false) MultipartFile[] file, BindingResult bindingResult) {
        try {
            // 유효성 검사
            projectService.validation(bindingResult, "title");
            projectService.validation(bindingResult, "description");
            projectService.validation(bindingResult, "deadlineAt");

            String projectImageFilePath;
//            if(file != null && file.length > 0 && !file[0].isEmpty()) {
//                projectImageFilePath = projectService.uploadProjectImage(file, dto.getTitle(), userInfo.getEmail());
//            } else {
//                projectImageFilePath = userInfo.getImage();
//            }

            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "팀플 모집 글쓰기 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
