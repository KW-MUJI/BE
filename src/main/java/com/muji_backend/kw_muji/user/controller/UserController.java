package com.muji_backend.kw_muji.user.controller;

import com.muji_backend.kw_muji.common.config.jwt.JwtProperties;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.entity.enums.UserRole;
import com.muji_backend.kw_muji.common.security.TokenProvider;
import com.muji_backend.kw_muji.user.dto.request.*;
import com.muji_backend.kw_muji.user.dto.response.TokenDTO;
import com.muji_backend.kw_muji.user.dto.response.UserResponseDTO;
import com.muji_backend.kw_muji.user.service.MailSendService;
import com.muji_backend.kw_muji.user.service.RedisService;
import com.muji_backend.kw_muji.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class UserController {
    private final MailSendService mailSendService;
    private final UserService userService;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signUp")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody @Valid SignUpRequestDTO dto, BindingResult bindingResult) {
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
                    .role(UserRole.USER) // 기본으로 USER 설정
                    .build();

            userService.createUser(user);
            redisService.deleteData(dto.getAuthNum());

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원가입 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/mailSend")
    public ResponseEntity<Map<String, Object>> mailSend(@RequestBody @Valid EmailRequestDTO dto, BindingResult bindingResult) {
        try {
            if (dto.getFlag()) { // 회원가입
                userService.validation(bindingResult, "email");

                if (userService.duplicateEmail(dto.getEmail()))
                    throw new IllegalArgumentException("이미 가입된 이메일");
            } else { // 비밀번호 찾기
                if (!userService.duplicateEmail(dto.getEmail()))
                    throw new IllegalArgumentException("가입되지 않은 이메일");
            }

            return ResponseEntity.ok().body(Map.of("code", 200, "data", mailSendService.joinEmail(dto.getEmail())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "이메일 전송 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/authCheck")
    public ResponseEntity<Map<String, Object>> authCheck(@RequestBody @Valid AuthNumRequestDTO dto) {
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
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody @Valid SignInRequestDTO dto, BindingResult bindingResult) {
        try {
            userService.validation(bindingResult, "email");

            UserEntity user = userService.getByCredentials(dto.getEmail(), dto.getPassword(), pwEncoder);

            if (user.getId() == null) {
                throw new IllegalArgumentException("로그인 실패");
            }

            final String accessToken = tokenProvider.createAccessToken(Optional.of(user));
            final String refreshToken = tokenProvider.createRefreshToken(Optional.of(user));

            final TokenDTO resDTO = TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok().body(Map.of("code", 200, "data", resDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "로그인 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/findPw")
    public ResponseEntity<Map<String, Object>> findPassword(@RequestBody AuthNumRequestDTO dto) {
        try {
            // 인증번호 확인
            if (!mailSendService.CheckAuthNum(dto.getEmail(), dto.getAuthNum()))
                throw new IllegalArgumentException("인증번호가 일치하지 않음");

            redisService.deleteData(dto.getAuthNum());
            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "비밀번호 찾기 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/resetPw")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody @Valid ResetPWRequestDTO dto, BindingResult bindingResult) {
        try {
            userService.validation(bindingResult, "password");

            if (!dto.getPassword().equals(dto.getConfirmPassword()))
                throw new IllegalArgumentException("비밀번호가 일치하지 않음");

            UserEntity user = UserEntity.builder()
                    .password(pwEncoder.encode(dto.getPassword()))
                    .build();

            userService.updatePw(dto.getEmail(), user);

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "비밀번호 재설정 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    // accessToken 재발급
    @PostMapping("/newToken")
    public ResponseEntity<?> createNewToken(HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization").substring(7);
            log.info("create new accessToken from : {}", token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();

            Long id = Long.parseLong(claims.getSubject());

            Optional<UserEntity> user = userService.getById(id);
            final UserResponseDTO resUserDTO = UserResponseDTO.builder()
                    .id(user.get().getId())
                    .email(user.get().getEmail())
                    .accessToken(tokenProvider.createAccessToken(user))
                    .build();

            return ResponseEntity.ok().body(resUserDTO);
        }catch (Exception e){
            log.error("/auth/newToken 실행 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("newToken fail");
        }
    }

    // refreshToken 재발급
    @PostMapping("/newRefreshToken")
    public ResponseEntity<?> createNewRefreshToken(HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization").substring(7);
            log.info("create new refresh Token from : {}", token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();

            Long id = Long.parseLong(claims.getSubject());

            Optional<UserEntity> user = userService.getById(id);
            final UserResponseDTO resUserDTO = UserResponseDTO.builder()
                    .id(user.get().getId())
                    .email(user.get().getEmail())
                    .refreshToken(tokenProvider.createRefreshToken(user))
                    .build();

            return ResponseEntity.ok().body(resUserDTO);
        }catch (Exception e){
            log.error("/auth/newrefreshToken 실행 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("newRefreshToken fail");
        }
    }
}
