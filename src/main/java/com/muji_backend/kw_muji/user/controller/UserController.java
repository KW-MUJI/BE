package com.muji_backend.kw_muji.user.controller;

import com.muji_backend.kw_muji.kwnotice.dto.response.NoticeResponse;
import com.muji_backend.kw_muji.user.service.MailSendService;
import com.muji_backend.kw_muji.user.util.ValidCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.muji_backend.kw_muji.user.dto.EmailRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class UserController {
    private final MailSendService mailSendService;

//    @PostMapping("/signUp")
//    public ResponseEntity<>

    @PostMapping("/mailSend")
    public ResponseEntity<Map<String, Object>> mailSend(@RequestBody @Valid EmailRequest dto) {
        try {
            if(!ValidCheck.isValidMail(dto.getEmail())) {
                throw new IllegalArgumentException("이메일이 규칙에 맞지 않음");
            }

            // 메일 주소 중복 검사

            String authNum = mailSendService.joinEmail(dto.getEmail());

            return ResponseEntity.ok().body(Map.of("code", 200, "authNum", authNum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "이메일 전송 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
