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
    private final Redis redis;
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
        String title = "인증 이메일"; // 이메일 제목
        String content =
                "광운 대학 생활 도우미를 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authNum + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요"; //이메일 내용 삽입
        mailSend(setFrom, email, title, content, authNum);

        return Integer.toString(authNum);
    }

    //이메일을 전송합니다.
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
