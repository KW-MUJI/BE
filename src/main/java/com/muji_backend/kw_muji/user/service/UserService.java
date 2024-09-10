package com.muji_backend.kw_muji.user.service;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepo;

    public Boolean duplicateEmail(final String email) {
        return userRepo.existsByEmail(email);
    }

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }

    public void createUser(final UserEntity entity) {
        if(entity == null) {
            throw new IllegalArgumentException("회원 정보가 비어있음");
        }

        userRepo.save(entity);
    }

    public UserEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
        UserEntity originalUser = userRepo.findByEmail(email);

        if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        } else if(originalUser == null) {
            throw new IllegalArgumentException("잘못된 이메일");
        } else if(!encoder.matches(password, originalUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀림");
        } else {
            throw new IllegalArgumentException("오류 발생");
        }
    }
}
