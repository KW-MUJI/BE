package com.muji_backend.kw_muji.user.controller;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.security.TokenProvider;
import com.muji_backend.kw_muji.user.dto.SignInDTO;
import com.muji_backend.kw_muji.user.dto.SignUpDTO;
import com.muji_backend.kw_muji.user.service.MailSendService;
import com.muji_backend.kw_muji.user.service.RedisService;
import com.muji_backend.kw_muji.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.muji_backend.kw_muji.user.dto.EmailDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
    private final UserService userService;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signUp")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody @Valid SignUpDTO dto, BindingResult bindingResult) {
        try {
            // 유저 유효성 검사
            userService.validation(bindingResult, "name");
            userService.validation(bindingResult, "stuNum");
            userService.validation(bindingResult, "major");
            userService.validation(bindingResult, "email");
            userService.validation(bindingResult, "authNum");
            userService.validation(bindingResult, "password");
            userService.validation(bindingResult, "confirmPassword");

            if (!dto.getPassword().equals(dto.getConfirmPassword()))
                throw new IllegalArgumentException("비밀번호가 일치하지 않음");

            // 인증번호 확인
            if (!mailSendService.CheckAuthNum(dto.getEmail(), dto.getAuthNum()))
                throw new IllegalArgumentException("인증번호가 일치하지 않음");

            UserEntity user = UserEntity.builder()
                    .name(dto.getName())
                    .stuNum(dto.getStuNum())
                    .major(dto.getMajor())
                    .email(dto.getEmail())
                    .password(pwEncoder.encode(dto.getPassword()))
                    .build();

            userService.createUser(user);
            redisService.deleteData(dto.getAuthNum());

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원가입. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/mailSend")
    public ResponseEntity<Map<String, Object>> mailSend(@RequestBody @Valid EmailDTO dto, BindingResult bindingResult) {
        try {
            userService.validation(bindingResult, "email");

            if (userService.duplicateEmail(dto.getEmail()))
                throw new IllegalArgumentException("이미 가입된 이메일");

            return ResponseEntity.ok().body(Map.of("code", 200, "data", mailSendService.joinEmail(dto.getEmail())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "이메일 전송 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/authCheck")
    public ResponseEntity<Map<String, Object>> authCheck(@RequestBody @Valid EmailDTO dto) {
        try {
            if (!mailSendService.CheckAuthNum(dto.getEmail(), dto.getAuthNum()))
                throw new IllegalArgumentException("인증번호가 일치하지 않음");

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "인증번호 확인 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody @Valid SignInDTO dto, BindingResult bindingResult) {
        try {
            userService.validation(bindingResult, "email");

            UserEntity user = userService.getByCredentials(dto.getEmail(), dto.getPassword(), pwEncoder);

            if (user.getId() == null) {
                throw new IllegalArgumentException("로그인 실패");
            }

            final String accessToken = tokenProvider.createAccessToken(user);
            final String refreshToken = tokenProvider.createRefreshToken(user);
            log.info("accessToken value: {}", accessToken);
            log.info("refreshToken value: {}", refreshToken);

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "로그인 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
