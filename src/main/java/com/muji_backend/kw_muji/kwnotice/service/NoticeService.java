package com.muji_backend.kw_muji.kwnotice.service;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;

import java.util.List;

public interface NoticeService {
    List<NoticeResponse> getKwHomeNotices(int page, String searchVal, String srCategoryId);
}