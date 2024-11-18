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

    public NoticeResponse parse(Document doc, String baseUrl) {
        List<NoticeResponse.Notice>  notices = new ArrayList<>();
        Elements elements = doc.select("div.board-list-box ul li");

        for (Element element : elements) {
            String category = element.select(CATEGORY_SELECTOR).text();
            String title = element.select(TITLE_SELECTOR).text()
                    .replace(category, "").replace("신규게시글", "").replace("Attachment", "").trim();
            String link = baseUrl + element.select(TITLE_SELECTOR).attr("href");
            String info = element.select(INFO_SELECTOR).text();

            String[] infoParts = info.split("\\|");
            String views = infoParts[0].trim().replace("조회수 ", "");
            String createdDate = infoParts[1].trim().replace("작성일 ", "");
            String updatedDate = infoParts[2].trim().replace("수정일 ", "");
            String team = infoParts[3].trim();

            NoticeResponse.Notice notice = new NoticeResponse.Notice(
                    category, title, link, views, createdDate, updatedDate, team
            );
            notices.add(notice);
        }

        // 최대 페이지 추출
        int maxPage = 1;
        Element lastPageElement = doc.select(LAST_PAGE_SELECTOR).first();
        if (lastPageElement != null) {
            String lastPageHref = lastPageElement.attr("href");
            maxPage = Integer.parseInt(lastPageHref.split("tpage=")[1].split("&")[0]);
        }

        return new NoticeResponse(notices, maxPage);
    }
}