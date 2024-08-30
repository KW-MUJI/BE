package com.muji_backend.kw_muji.kwnotice.controller;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/api/notices")
    public ResponseEntity<Map<String, Object>> getKwHomeNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String searchVal,
            @RequestParam(required = false) String srCategoryId) {
        try {
            if (page <= 0) {
                throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다.");
            }
            NoticeResponse notices = noticeService.getKwHomeNotices(page, searchVal, srCategoryId);
            return ResponseEntity.ok().body(Map.of("code", 200, "data", notices));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "처리 중에 문제가 발생했습니다. 잠시 후 다시 시도해주세요."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "공지사항을 가져오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }
}