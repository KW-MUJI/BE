package com.muji_backend.kw_muji.survey.controller;

import com.muji_backend.kw_muji.survey.dto.response.MySurveyResponseDto;
import com.muji_backend.kw_muji.survey.service.MySurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{surveyId}")
    public ResponseEntity<?> endSurvey(@PathVariable Long surveyId) {
        try {
            Long updatedSurveyId = mySurveyService.endSurvey(surveyId);
            return ResponseEntity.ok().body(Map.of("code", 200, "surveyId", updatedSurveyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{surveyId}")
    public ResponseEntity<?> deleteSurvey(@PathVariable Long surveyId) {
        try {
            mySurveyService.deleteSurvey(surveyId);
            return ResponseEntity.ok().body(Map.of("code", 200, "message", "설문조사 삭제 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
}