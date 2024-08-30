package com.muji_backend.kw_muji.kwnotice.service;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public NoticeResponse getKwHomeNotices(int page, String searchVal, String srCategoryId) {
        return noticeRepository.getKwHomeNotices(page, searchVal, srCategoryId);
    }
}
