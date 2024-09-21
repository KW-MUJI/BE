package com.muji_backend.kw_muji.mypage.controller;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.mypage.dto.UserInfoDTO;
import com.muji_backend.kw_muji.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;

    private final PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @PostMapping("/checkPw")
    public ResponseEntity<Map<String, Object>> enterUpdate(@AuthenticationPrincipal UserEntity userInfo, @RequestBody UserInfoDTO dto) {
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

            final UserInfoDTO resDTO = UserInfoDTO.builder()
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
}
