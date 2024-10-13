package com.muji_backend.kw_muji.team.controller;

import com.muji_backend.kw_muji.team.dto.response.MyProjectResponseDTO;
import com.muji_backend.kw_muji.team.service.MyTeamService;
import org.springframework.http.ResponseEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/myteam")
public class MyTeamController {
    private final MyTeamService myTeamService;

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

//    @GetMapping("/applicant")
//    public ResponseEntity<Map<String, Object>> getApplicants(@AuthenticationPrincipal UserEntity userInfo) {
//        try {
//
//
//            return org.springframework.http.ResponseEntity.ok().body(Map.of("code", 200, "data", true));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "MY 모집 팀플 로딩 오류. 잠시 후 다시 시도해주세요."));
//        }
//    }
}
