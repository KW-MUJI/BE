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

    public void joinEmail(String email, String projectName) {
        String setFrom = username; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "광운 대학 생활 도우미 : " + projectName + " 프로젝트 시작 알림"; // 이메일 제목

        // HTML 내용
        String content =
            "<div>" +
            "    <table align='center' width='100%' style='padding: 60px 0; color: #555; font-size: 16px; word-break: keep-all;'>" +
            "        <tbody>" +
            "            <tr>" +
            "                <td>" +
            "                    <table align='center' style='width: 100%; max-width: 600px; margin: 0 auto; background: #fff;'>" +
            "                        <tbody>" +
            "                            <!-- 로고 영역 -->" +
            "                            <tr>" +
            "                                <td style='padding-bottom: 22px; text-align: center;'>" +
            "                                    <div style='font-size: 22px; font-weight: 700; color: #000;'>" +
            "                                        광운 대학 생활 도우미" +
            "                                        <br/>" +
            "                                        <span style='color: #8b0b02;'>광운 무인양품</span>" +
            "                                    </div>" +
            "                                </td>" +
            "                            </tr>" +
            "                            <!-- 본문 영역 -->" +
            "                            <tr>" +
            "                                <td style='border: 10px solid #f2f2f2; padding: 60px 14px; text-align: center;'>" +
            "                                    <table align='center' style='max-width: 630px; margin: 0 auto; letter-spacing: -1px;'>" +
            "                                        <tbody>" +
            "                                            <tr>" +
            "                                                <td style='font-size: 16px; line-height: 24px; color: #000; text-align: left; font-family: \"Nanum Gothic\", \"맑은 고딕\", \"Malgun Gothic\", \"돋움\", \"Dotum\", Helvetica, \"Apple SD Gothic Neo\", sans-serif;'>" +
            "                                                    <p style='margin: 0; padding-bottom: 14px; font-weight: 700;'>" +
            "                                                        축하드립니다! 신청하신 팀 프로젝트가 시작되었습니다." +
            "                                                    </p>" +
            "                                                    <p style='margin: 0;'>" +
            "                                                        <span style='font-weight: bold;'>마이페이지</span>에서 확인 가능합니다." +
            "                                                    </p>" +
            "                                                    <p style='margin: 0; padding-bottom: 14px;'>" +
            "                                                        프로젝트 이름: <span style='font-weight: bold; color: #8b0b02;'>" + projectName + "</span>" +
            "                                                    </p>" +
            "                                                </td>" +
            "                                            </tr>" +
            "                                        </tbody>" +
            "                                    </table>" +
            "                                </td>" +
            "                            </tr>" +
            "                        </tbody>" +
            "                    </table>" +
            "                </td>" +
            "            </tr>" +
            "        </tbody>" +
            "    </table>" +
            "</div>";

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
