package com.muji_backend.kw_muji.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailSendService {
    private final RedisService redis;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public boolean CheckAuthNum(String email, String authNum) {
        if(redis.getData(authNum) == null)
            return false;
        else if(redis.getData(authNum).equals(email))
            return true;
        else
            return false;
    }

    public int makeRandomNumber() {
        Random r = new Random();
        String randomNum = "";

        for(int i = 0; i < 6; i++)
            randomNum += Integer.toString(r.nextInt(9) + 1);

        return Integer.parseInt(randomNum);
    }

    public String joinEmail(String email) {
        int authNum = makeRandomNumber();
        String setFrom = username; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "광운 대학 생활 도우미 : 회원가입 인증번호"; // 이메일 제목

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
            "                                                        인증 번호 확인 후<br>" +
            "                                                        이메일 인증을 완료해주세요." +
            "                                                    </p>" +
            "                                                    <p style='margin: 0; padding-bottom: 14px;'>" +
            "                                                        안녕하세요? <span style='font-weight: bold;'>광운 무인양품</span>입니다." +
            "                                                    </p>" +
            "                                                    <p style='margin: 0; padding-bottom: 14px;'>" +
            "                                                        아래 인증번호를 입력하여 이메일 인증을 완료해주세요." +
            "                                                    </p>" +
            "                                                    <p style='margin: 0; font-weight: bold; color: #8b0b02;'>" +
            "                                                        인증번호 : " + authNum +
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

        mailSend(setFrom, email, title, content, authNum);

        return Integer.toString(authNum);
    }

    public void mailSend(String setFrom, String toMail, String title, String content, int authNum) {
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
        redis.setDataExpire(Integer.toString(authNum),toMail,60*5L);
    }
}
