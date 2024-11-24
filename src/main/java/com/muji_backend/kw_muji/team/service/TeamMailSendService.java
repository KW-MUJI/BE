package com.muji_backend.kw_muji.team.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Slf4j
@Service
public class TeamMailSendService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void joinEmail(String email) {
        String setFrom = username; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "팀플 시작 알림 이메일"; // 이메일 제목
        String content =
                "광운 대학 생활 도우미입니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "신청하신 팀프로젝트가 시작되었습니다." +
                        "<br>" +
                        "마이페이지에서 확인 가능합니다."; //이메일 내용 삽입
        mailSend(setFrom, email, title, content);
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");

            helper.setFrom(setFrom, "광운 대학 생활 도우미");
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
