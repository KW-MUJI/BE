package com.muji_backend.kw_muji.common.security;

import com.muji_backend.kw_muji.common.config.jwt.JwtProperties;
import com.muji_backend.kw_muji.common.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String createAccessToken(Optional<UserEntity> user){
        log.info("creating access token");

        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        log.info("set access token expiryDate: {}", expiryDate);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512,jwtProperties.getSecretKey())
                .setSubject(String.valueOf(user.get().getId())) // 토큰 제목
                .setIssuer(jwtProperties.getIssuer()) // 토큰 발급자
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .claim("id", user.get().getId()) // 토큰에 사용자 아이디 추가하여 전달
                .claim("email", user.get().getEmail())
                .compact(); // 토큰 생성
    }

    public String createRefreshToken(Optional<UserEntity> user){
        log.info("creating refresh token");

        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        log.info("set refresh token expiryDate: {}", expiryDate);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512,jwtProperties.getSecretKey())
                .setSubject(String.valueOf(user.get().getId()))
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("id", user.get().getId())
                .claim("email", user.get().getEmail())
                .compact();
    }

    // 토큰 검증 및 토큰에 포함된 정보를 추출하여 인증 및 권한 부여
    public Claims validateAndGetClaims(String token) {
        log.info("extract");
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Token expired date : {}", claims.getExpiration());
            log.info("date now: {}", Date.from(Instant.now()));

            return claims;
        }catch (ExpiredJwtException e){
            log.warn("ExpiredJwtException!!");
            Claims claims = Jwts.claims().setIssuer("Expired");

            return claims;
        }catch (Exception e) {
            log.warn("Exception : {}", e.getMessage());
            Claims claims = Jwts.claims().setIssuer("Token error");

            return claims;
        }
    }
}
