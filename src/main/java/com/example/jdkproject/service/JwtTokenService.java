package com.example.jdkproject.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class JwtTokenService {

    // 토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;

    // JWT 토큰 생성
    public String createToken(String memberId, String name, String email, PrivateKey privateKey) {
        Claims claims = Jwts.claims().setSubject(memberId); // JWT payload 에 저장되는 정보단위
        claims.put("name", name); // 정보는 key / value 쌍으로 저장된다.
        claims.put("email", email); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.RS512, privateKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
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
}
