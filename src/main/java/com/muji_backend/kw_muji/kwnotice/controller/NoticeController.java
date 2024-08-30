package com.muji_backend.kw_muji.kwnotice.controller;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/api/notices")
    public List<NoticeResponse> getKwHomeNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String searchVal,
            @RequestParam(required = false) String srCategoryId) {
        return noticeService.getKwHomeNotices(page, searchVal, srCategoryId);
    }
}