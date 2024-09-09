package com.muji_backend.kw_muji.user.service;

import com.muji_backend.kw_muji.common.entity.UserEntity;
import com.muji_backend.kw_muji.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepo;

    public Boolean duplicateEmail(final String email) {
        return userRepo.existsByEmail(email);
    }

    public void createUser(final UserEntity entity) {
        if(entity == null) {
            throw new RuntimeException("회원 정보가 비어있음");
        }

        userRepo.save(entity);
    }
}
