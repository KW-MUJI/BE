package com.muji_backend.kw_muji.kwnotice.util;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NoticeUrlParser {

    private static final String CATEGORY_SELECTOR = "strong.category";
    private static final String TITLE_SELECTOR = "div.board-text a";
    private static final String INFO_SELECTOR = "p.info";
    private static final String LAST_PAGE_SELECTOR = "a.ico-page.last";
    private static final String EMPTY_NOTICE_SELECTOR = "li.list_none";

    /**
     * HTML 문서를 파싱하여 공지사항 목록과 최대 페이지 번호를 추출하는 메서드
     *
     * @param doc     공지사항 데이터가 포함된 HTML 문서
     * @param baseUrl 공지사항 링크를 구성하기 위한 기본 URL
     * @return 공지사항 목록과 최대 페이지 번호를 포함한 NoticeResponse 객체
     */
    public NoticeResponse parse(Document doc, String baseUrl) {
        try {
            // 게시물이 없는 경우 처리
            if (isEmptyNotice(doc)) {
                return new NoticeResponse(new ArrayList<>(), 1); // 빈 목록과 기본 페이지 반환
            }

            // 공지사항 파싱
            List<NoticeResponse.Notice> notices = parseNotices(doc, baseUrl);

            // 최대 페이지 번호 추출
            int maxPage = extractMaxPage(doc);

            return new NoticeResponse(notices, maxPage);
        } catch (Exception e) {
            throw new RuntimeException("공지사항 데이터를 파싱하는 중 오류가 발생했습니다.", e);
        }
    }

    // == Private Methods ==

    // 게시물이 없는지 확인
    private boolean isEmptyNotice(Document doc) {
        Element emptyNoticeElement = doc.selectFirst(EMPTY_NOTICE_SELECTOR);
        return emptyNoticeElement != null && "등록된 글이 없습니다.".equals(emptyNoticeElement.text().trim());
    }

    // 공지사항 doc 파싱
    private List<NoticeResponse.Notice> parseNotices(Document doc, String baseUrl) {
        List<NoticeResponse.Notice> notices = new ArrayList<>();
        Elements elements = doc.select("div.board-list-box ul li");

        for (Element element : elements) {
            notices.add(parseNoticeElement(element, baseUrl));
        }
        return notices;
    }

    // 개별 공지사항 요소를 파싱
    private NoticeResponse.Notice parseNoticeElement(Element element, String baseUrl) {
        String category = element.select(CATEGORY_SELECTOR).text();
        String title = element.select(TITLE_SELECTOR).text()
                .replace(category, "").replace("신규게시글", "").replace("Attachment", "").trim();
        String link = baseUrl + element.select(TITLE_SELECTOR).attr("href");
        String info = element.select(INFO_SELECTOR).text();

        // 정보 문자열 파싱
        String[] infoParts = info.split("\\|");
        String views = parseInfoPart(infoParts, 0).replace("조회수 ", "");
        String createdDate = parseInfoPart(infoParts, 1).replace("작성일 ", "");
        String updatedDate = parseInfoPart(infoParts, 2).replace("수정일 ", "");
        String team = parseInfoPart(infoParts, 3);

        return new NoticeResponse.Notice(category, title, link, views, createdDate, updatedDate, team);
    }

    // 문자열 배열에서 특정 인덱스의 데이터를 추출
    private String parseInfoPart(String[] infoParts, int index) {
        if (index < infoParts.length) {
            return infoParts[index].trim();
        }
        return ""; // 기본값
    }

    // 최대 페이지 번호를 추출
    private int extractMaxPage(Document doc) {
        Element lastPageElement = doc.selectFirst(LAST_PAGE_SELECTOR);
        if (lastPageElement != null) {
            String lastPageHref = lastPageElement.attr("href");
            return Integer.parseInt(lastPageHref.split("tpage=")[1].split("&")[0]);
        }
        return 1; // 기본값
    }
}