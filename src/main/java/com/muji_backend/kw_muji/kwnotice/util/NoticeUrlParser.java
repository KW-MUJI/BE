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

    public List<NoticeResponse> parse(Document doc, String baseUrl) {
        List<NoticeResponse> notices = new ArrayList<>();
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

            NoticeResponse notice = new NoticeResponse(
                    category, title, link, views, createdDate, updatedDate, team
            );
            notices.add(notice);
        }

        return notices;
    }
}