package com.example.jdkproject.service;

import com.example.jdkproject.dto.JtiInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    // 토큰 유효시간 60분
    private long tokenValidTime = 60 * 60 * 1000L;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // JWT 토큰 생성
    public String createToken(String memberId, String name, String email, PrivateKey privateKey) {

        String jti = createJti();
        JtiInfo jtiInfo = getJtiInfo(memberId, name, email);

        try {
            String jtiString = objectMapper.writeValueAsString(jtiInfo);
            saveToRedis(jti, jtiString);
        } catch (Exception e) {
            log.error("Jti store error: ", e);
        }

        Date now = new Date();

        String accessToken = Jwts.builder()
                .subject(memberId)
                .claim("jti", jti)
                .claim("iss", "jungs.com")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + tokenValidTime))
                .signWith(privateKey, Jwts.SIG.RS512)
                .compact();

        // redis 등록
        saveToRedis(memberId, accessToken);

        return accessToken;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken, PublicKey publicKey) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(jwtToken);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String createJti() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    private JtiInfo getJtiInfo(String memberId, String name, String email) {
        return JtiInfo.builder()
                .memberId(memberId)
                .name(name)
                .email(email)
                .build();
    }

    private void saveToRedis(String key, String data) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data, 3600000, TimeUnit.MILLISECONDS);
    }
}
