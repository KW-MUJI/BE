package com.muji_backend.kw_muji.kwnotice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponse {

    private List<Notice> notices;
    private int maxPage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Notice {
        private String category;
        private String title;
        private String link;
        private String views;
        private String createdDate;
        private String updatedDate;
        private String team;
    }
}
