package com.muji_backend.kw_muji.kwnotice.repository;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.kwnotice.util.NoticeUrlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {

    @Value("${kw.notice.url}")
    private String kwHomeNoticesUrl;

    @Value("${base.url}")
    private String BASE_URL;

    @Autowired
    NoticeUrlParser parser;

    @Override
    public List<NoticeResponse> getKwHomeNotices(int page, String searchVal, String srCategoryId) {
        try {
            String url = buildUrl(page, searchVal, srCategoryId);
            Document doc = fetchDocument(url);
            return parser.parse(doc, BASE_URL);
        } catch (IOException e) {
            throw new RuntimeException("공지사항을 가져오는 데 실패했습니다.", e);
        }
    }

    private String buildUrl(int page, String searchVal, String srCategoryId) {
        StringBuilder urlWithParams = new StringBuilder(kwHomeNoticesUrl + "?tpage=" + page);

        if (searchVal != null && !searchVal.isEmpty()) {
            urlWithParams.append("&searchVal=").append(URLEncoder.encode(searchVal, StandardCharsets.UTF_8));
        }

        if (srCategoryId != null && !srCategoryId.isEmpty()) {
            urlWithParams.append("&srCategoryId=").append(srCategoryId);
        }

        return urlWithParams.toString();
    }

    private Document fetchDocument(String url) throws IOException {
        // 웹이 요청을 브라우저에서 온 것처럼 인식하도록 설정
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .get();
    }
}