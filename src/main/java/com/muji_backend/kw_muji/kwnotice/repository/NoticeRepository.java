package com.muji_backend.kw_muji.kwnotice.repository;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;

import java.util.List;

public interface NoticeRepository {
    List<NoticeResponse> getKwHomeNotices(int page, String searchVal, String srCategoryId);
}