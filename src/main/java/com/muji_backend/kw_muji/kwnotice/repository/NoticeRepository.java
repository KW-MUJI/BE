package com.muji_backend.kw_muji.kwnotice.repository;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;

public interface NoticeRepository {
    NoticeResponse getKwHomeNotices(int page, String searchVal, String srCategoryId);
}