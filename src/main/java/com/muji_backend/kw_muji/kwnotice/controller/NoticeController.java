package com.muji_backend.kw_muji.kwnotice.controller;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notices")
    public ResponseEntity<Map<String, Object>> getKwHomeNotices(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam(value = "srCategoryId", required = false) String srCategoryId) {
        try {
            if (page <= 0) {
                throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다.");
            }
            NoticeResponse notices = noticeService.getKwHomeNotices(page, searchVal, srCategoryId);
            return ResponseEntity.ok().body(Map.of("code", 200, "data", notices));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }
}