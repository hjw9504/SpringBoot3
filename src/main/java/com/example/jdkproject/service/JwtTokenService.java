package com.example.jdkproject.service;

import com.example.jdkproject.dto.JtiInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    // 토큰 유효시간 30분
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

        Claims claims = Jwts.claims().setSubject(memberId); // JWT payload 에 저장되는 정보단위
        claims.put("jti", jti); // 정보는 key / value 쌍으로 저장된다.
        claims.put("iss", "jungs.com");
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(memberId)
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.RS512, privateKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        //redis 등록
        saveToRedis(memberId, accessToken);

        return accessToken;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken, PublicKey publicKey) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
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
