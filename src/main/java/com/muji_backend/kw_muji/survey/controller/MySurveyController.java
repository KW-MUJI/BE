package com.muji_backend.kw_muji.survey.controller;

import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.survey.service.MySurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mysurvey")
public class MySurveyController {

    private final MySurveyService mySurveyService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMySurveys(@PathVariable Long userId) {
        try {
            List<MySurveyResponseDto> surveys = mySurveyService.getSurveysByUserId(userId);
            return ResponseEntity.ok().body(Map.of("code", 200, "data", surveys));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
}