package com.muji_backend.kw_muji.calendar.controller;

import com.muji_backend.kw_muji.calendar.service.CalendarService;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{yearMonth}")
    public ResponseEntity<?> getCalendar(@AuthenticationPrincipal UserEntity userInfo,
                                                           @PathVariable String yearMonth) {
        try {

            return ResponseEntity.ok().body(Map.of("code", 200, "data", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
}