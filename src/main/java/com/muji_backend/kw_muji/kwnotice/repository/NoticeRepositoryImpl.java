package com.muji_backend.kw_muji.kwnotice.repository;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {

    @Value("${kw.notice.url}")
    private String kwHomeNoticesUrl;

    @Value("${base.url}")
    private String BASE_URL;

    @Override
    public List<NoticeResponse> getKwHomeNotices(int page, String searchVal, String srCategoryId) {
        List<NoticeResponse> notices = new ArrayList<>();
        try {
            // 페이지 번호에 따라 URL 기본 설정
            StringBuilder urlWithParams = new StringBuilder(kwHomeNoticesUrl + "?tpage=" + page);

            // searchVal가 있으면 추가
            if (searchVal != null && !searchVal.isEmpty()) {
                String encodedSearchVal = URLEncoder.encode(searchVal, StandardCharsets.UTF_8.toString());
                urlWithParams.append("&searchVal=").append(encodedSearchVal);
            }

            // srCategoryId가 있으면 추가
            if (srCategoryId != null && !srCategoryId.isEmpty()) {
                urlWithParams.append("&srCategoryId=").append(srCategoryId);
            }

            // Jsoup을 사용하여 HTML 문서 가져오기
            Document doc = Jsoup.connect(urlWithParams.toString())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();

            // 공지사항 목록 파싱
            Elements elements = doc.select("div.board-list-box ul li");

            for (Element element : elements) {
                String category = element.select("strong.category").text();  // 카테고리 추출
                String title = element.select("div.board-text a").text();  // 제목 추출

                // 제목에서 카테고리와 "신규게시글" 제거
                title = title.replace(category, "").replace("신규게시글", "").replace("Attachment", "").trim();

                String link = BASE_URL + element.select("div.board-text a").attr("href");  // 링크 추출 (BASE_URL 추가)
                String info = element.select("p.info").text();  // 조회수, 작성일, 수정일, 팀 정보 추출

                // info에서 세부 정보 추출
                String[] infoParts = info.split("\\|");
                String views = infoParts[0].trim().replace("조회수 ", "");
                String createdDate = infoParts[1].trim().replace("작성일 ", "");
                String updatedDate = infoParts[2].trim().replace("수정일 ", "");
                String team = infoParts[3].trim();

                NoticeResponse notice = new NoticeResponse();
                notice.setCategory(category);
                notice.setTitle(title);
                notice.setLink(link);
                notice.setViews(views);
                notice.setCreatedDate(createdDate);
                notice.setUpdatedDate(updatedDate);
                notice.setTeam(team);

                notices.add(notice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notices;
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