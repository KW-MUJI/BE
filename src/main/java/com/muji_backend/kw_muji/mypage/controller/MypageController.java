package com.muji_backend.kw_muji.mypage.controller;

import com.muji_backend.kw_muji.common.entity.ResumeEntity;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.common.security.TokenProvider;
import com.muji_backend.kw_muji.mypage.dto.request.PasswordRequestDTO;
import com.muji_backend.kw_muji.mypage.dto.request.UpdateRequestDTO;
import com.muji_backend.kw_muji.mypage.dto.response.MyProjectsResponseDTO;
import com.muji_backend.kw_muji.mypage.dto.response.TokenDTO;
import com.muji_backend.kw_muji.mypage.dto.response.UserInfoResponseDTO;
import com.muji_backend.kw_muji.mypage.service.MypageService;
import com.muji_backend.kw_muji.mypage.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;
    private final ResumeService resumeService;
    private final TokenProvider tokenProvider;

    private final PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @Value("${cloud.aws.s3.url}")
    private String bucketURL;

    @PostMapping("/checkPw")
    public ResponseEntity<Map<String, Object>> enterUpdate(@AuthenticationPrincipal UserEntity userInfo, @RequestBody PasswordRequestDTO dto) {
        try {
            if(!mypageService.equalPassword(userInfo.getEmail(), dto.getPassword(), pwdEncoder))
                throw new IllegalArgumentException("비밀번호가 일치하지 않음");

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
                    .image(user.getImage() != null ? bucketURL + URLEncoder.encode(user.getImage(), "UTF-8") : "")
                    .email(user.getEmail())
                    .name(user.getName())
                    .stuNum(user.getStuNum())
                    .major(user.getMajor())
                    .build();

            return ResponseEntity.ok().body(Map.of("code", 200, "data", resDTO));
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원정보 로딩 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUserInfo(@AuthenticationPrincipal UserEntity userInfo, @Valid UpdateRequestDTO dto, @RequestParam(value = "image", required = false) MultipartFile[] file, BindingResult bindingResult) {
        try {
            // 유효성 검사
            mypageService.validation(bindingResult, "name");
            mypageService.validation(bindingResult, "stuNum");
            mypageService.validation(bindingResult, "major");
            mypageService.validation(bindingResult, "password");
            mypageService.validation(bindingResult, "confirmPassword");

            if (!dto.getPassword().equals(dto.getConfirmPassword()))
                throw new IllegalArgumentException("비밀번호가 일치하지 않음");

            if (!dto.getPassword().isBlank())
                userInfo.setPassword(pwdEncoder.encode(dto.getPassword()));

            if(file != null && file.length > 0 && !file[0].isEmpty())
                userInfo.setImage(mypageService.uploadUserImage(file, dto.getName(), userInfo.getEmail()));
            else if (dto.isDeleteImage()) // 프로필 사진 삭제를 요청한 경우
                mypageService.deleteUserImage(userInfo.getEmail());

            final UserEntity updateUser = mypageService.updateUser(userInfo, dto);

            final TokenDTO resDTO = TokenDTO.builder()
                    .accessToken(tokenProvider.createAccessToken(updateUser))
                    .refreshToken(tokenProvider.createRefreshToken(updateUser))
                    .build();

            return ResponseEntity.ok().body(Map.of("code", 200, "data", resDTO));
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원정보 수정 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Map<String, Object>> deleteUser(@AuthenticationPrincipal UserEntity userInfo) {
        try {
            final UserEntity user = mypageService.originalUser(userInfo.getEmail());

            mypageService.deleteUser(user);

            return ResponseEntity.ok().body(Map.of("code", 200, "data", user.getName() + "의 계정 삭제"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원 탈퇴 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> registerResume(@AuthenticationPrincipal UserEntity userInfo, @RequestParam(value = "resume", required = false) MultipartFile[] file) {
        try {
            if(!resumeService.checkResumeCount(userInfo))
                throw new IllegalArgumentException("포트폴리오는 3개까지만 등록 가능");

            final ResumeEntity resume = new ResumeEntity();
            final UserEntity user = mypageService.originalUser(userInfo.getEmail());

            resume.setUsers(user);
            resume.setResumePath(resumeService.uploadResume(file, user.getName(), resume)); // 파일명, 파일 주소 모두 저장

            resumeService.saveResume(resume);

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원 탈퇴 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @DeleteMapping("/deleteResume/{resumeId}")
    public ResponseEntity<Map<String, Object>> deleteResume(@AuthenticationPrincipal UserEntity userInfo, @PathVariable Long resumeId) {
        try {
            final UserEntity user = mypageService.originalUser(userInfo.getEmail());
            final ResumeEntity resume = resumeService.getResume(resumeId, user);

            resumeService.deleteResume(resume);

            return ResponseEntity.ok().body(Map.of("code", 200, "data", user.getName() + "의 포트폴리오 삭제"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "회원 탈퇴 오류. 잠시 후 다시 시도해주세요."));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> main(@AuthenticationPrincipal UserEntity userInfo) {
        try {
            List<MyProjectsResponseDTO> projects = mypageService.getMyProjects(userInfo); // my 팀플

            return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "data", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "data", "마이페이지 로딩 오류. 잠시 후 다시 시도해주세요."));
        }
    }
}
