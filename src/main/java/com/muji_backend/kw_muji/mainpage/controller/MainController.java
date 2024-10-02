package com.muji_backend.kw_muji.mainpage.controller;

import com.muji_backend.kw_muji.mainpage.dto.response.MainResponseDto;
import com.muji_backend.kw_muji.mainpage.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mainpage")
public class MainController {

    private final MainService mainService;

    @GetMapping("/{yearMonth}")
    public ResponseEntity<?> getMainInfo(@PathVariable("yearMonth") String yearMonth) {
        try {
            MainResponseDto response = mainService.getMainInfo(yearMonth);
            return ResponseEntity.ok().body(Map.of("code", 200 , "data", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
}
