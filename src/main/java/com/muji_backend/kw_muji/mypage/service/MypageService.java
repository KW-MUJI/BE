package com.muji_backend.kw_muji.mypage.service;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.mypage.repository.MypageRepository;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
