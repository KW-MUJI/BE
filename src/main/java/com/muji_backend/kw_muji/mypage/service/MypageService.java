package com.muji_backend.kw_muji.mypage.service;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.mypage.dto.request.UpdateRequestDTO;
import com.muji_backend.kw_muji.mypage.repository.MypageRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class MypageService {
    private final MypageRepository mypageRepo;

    public Boolean equalPassword(final String email, final String password, final PasswordEncoder encoder) {
        final UserEntity user = mypageRepo.findByEmail(email);

        return user != null && encoder.matches(password, user.getPassword());
    }

    public UserEntity originalUser(final String email) {
        return mypageRepo.findByEmail(email);
    }

    @Transactional
    public UserEntity updateUser(final UserEntity userEntity, final UpdateRequestDTO dto) {
        final UserEntity user = originalUser(userEntity.getEmail());

        if(dto.getName() != null && !dto.getName().isBlank())
            user.setName(dto.getName());

        if(dto.getStuNum() > 0)
            user.setStuNum(userEntity.getStuNum());

        if(dto.getMajor() != null && !dto.getMajor().isBlank())
            user.setMajor(dto.getMajor());

        if(dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(dto.getPassword());

        return mypageRepo.save(user);
    }

    public void validation(BindingResult bindingResult, String fieldName) {
        if (bindingResult.hasFieldErrors(fieldName))
            throw new IllegalArgumentException(Objects.requireNonNull(bindingResult.getFieldError(fieldName)).getDefaultMessage());
    }
}
