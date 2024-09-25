package com.muji_backend.kw_muji.mypage.controller;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.security.TokenProvider;
import com.muji_backend.kw_muji.mypage.dto.request.PasswordRequestDTO;
import com.muji_backend.kw_muji.mypage.dto.request.UpdateRequestDTO;
import com.muji_backend.kw_muji.mypage.dto.response.TokenDTO;
import com.muji_backend.kw_muji.mypage.dto.response.UserInfoResponseDTO;
import com.muji_backend.kw_muji.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;
    private final TokenProvider tokenProvider;

    private final PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @PostMapping("/checkPw")
    public ResponseEntity<Map<String, Object>> enterUpdate(@AuthenticationPrincipal UserEntity userInfo, @RequestBody PasswordRequestDTO dto) {
        try {
            if(!mypageService.equalPassword(userInfo.getEmail(), dto.getPassword(), pwdEncoder)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않음");
            }

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "비밀번호 확인 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal UserEntity userInfo) {
        try {
            final UserEntity user = mypageService.originalUser(userInfo.getEmail());

            final UserInfoResponseDTO resDTO = UserInfoResponseDTO.builder()
                    .image(user.getImage())
                    .email(user.getEmail())
                    .name(user.getName())
                    .stuNum(user.getStuNum())
                    .major(user.getMajor())
                    .build();

            return ResponseEntity.ok().body(Map.of("code", 200, "data", resDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원정보 로딩 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUserInfo(@AuthenticationPrincipal UserEntity userInfo, @RequestBody UpdateRequestDTO dto, BindingResult bindingResult) {
        try {
            // 유효성 검사
            mypageService.validation(bindingResult, "name");
            mypageService.validation(bindingResult, "stuNum");
            mypageService.validation(bindingResult, "major");
            mypageService.validation(bindingResult, "password");
            mypageService.validation(bindingResult, "confirmPassword");

            if (!dto.getPassword().equals(dto.getConfirmPassword()))
                throw new IllegalArgumentException("비밀번호가 일치하지 않음");

            final UserEntity updateUser = mypageService.updateUser(userInfo, dto);

            final TokenDTO resDTO = TokenDTO.builder()
                    .accessToken(tokenProvider.createAccessToken(updateUser))
                    .refreshToken(tokenProvider.createRefreshToken(updateUser))
                    .build();

            return ResponseEntity.ok().body(Map.of("code", 200, "data", resDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "비밀번호 확인 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
