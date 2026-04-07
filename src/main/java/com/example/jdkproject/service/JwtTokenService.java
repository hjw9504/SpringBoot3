package com.example.jdkproject.service;

import com.example.jdkproject.domain.Member;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.dto.TokenDto;
import com.example.jdkproject.entity.MemberSecureVo;
import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberRepository;
import com.example.jdkproject.repository.MemberSecureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final MemberSecureRepository memberSecureRepository;
    private final RedisService redisService;

    // 토큰 유효시간 60분
    private final long accessTokenTtl = 60 * 60 * 1000L;
    private final long refreshTokenTtl = 60 * 60 * 24 * 7 * 1000L;
    private final String ACCESS_TOKEN_KEY = "auth:at:";
    private final String REFRESH_TOKEN_KEY = "auth:rt:";

    // JWT 토큰 생성
    public String createAccessToken(Member member, PrivateKey privateKey) {

        String jti = createJti();
        String key = ACCESS_TOKEN_KEY + member.getMemberId();

        Date now = new Date();

        String accessToken = Jwts.builder()
                .subject(member.getMemberId())
                .claim("jti", jti)
                .claim("iss", "jungs.com")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenTtl))
                .signWith(privateKey, Jwts.SIG.RS512)
                .compact();

        redisService.saveToRedis(jti, member.getMemberId(), accessTokenTtl);
        redisService.saveListToRedis(key, accessToken, accessTokenTtl);

        return accessToken;
    }

    public String createRefreshToken(Member member) {

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64]; // 64바이트 난수 생성
        secureRandom.nextBytes(randomBytes);

        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String refreshTokenKey = REFRESH_TOKEN_KEY + refreshToken;

        redisService.saveToRedis(refreshTokenKey, member.getMemberId(), refreshTokenTtl);

        return refreshToken;
    }

    public TokenDto getNewAccessTokenAndRefreshToken(String memberId, String refreshTokenKey) {
        try {
            MemberVo memberVo = memberRepository.findUserByMemberId(memberId)
                    .filter(list -> !list.isEmpty())
                    .map(List::getFirst)
                    .orElseThrow(() -> new CommonErrorException(ErrorStatus.NOT_FOUND));

            Member member = new Member().toMember(memberVo, memberVo.getUserId());

            MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberId);

            // create new refreshToken
            PrivateKey privateKey = getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey());
            String accessToken = createAccessToken(member, privateKey);
            String refreshToken = createRefreshToken(member);

            redisService.deleteFromRedis(refreshTokenKey);

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .memberId(memberId)
                    .build();
        } catch (Exception e) {
            log.error("Get new access token & refresh token error : ", e);
            throw new CommonErrorException(ErrorStatus.TOKEN_PARSE_ERROR);
        }
    }

    public JtiInfo verifyToken(String token) {
        // token payload parse
        Map<String, String> claims = parsePayload(token);

        // get jti and info from redis
        String jti = claims.get("jti");
        String memberId = redisService.getFromRedis(jti).toString();
        String key = ACCESS_TOKEN_KEY + memberId;

        // token 조회 (최근 허용 토큰 체크)
        List<String> tokenList = redisService.getListFromRedis(key);
        if (tokenList.isEmpty() || !tokenList.contains(token)) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }

        MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberId);
        if (memberSecureInfo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        validateAccessToken(token, getPublicKeyFromBase64Encrypted(memberSecureInfo.getPublicKey()));

        return JtiInfo.builder()
                .memberId(memberId)
                .build();
    }

    // 토큰의 유효성 + 만료일자 확인
    public void validateAccessToken(String jwtToken, PublicKey publicKey) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwtToken);

        if (claims.getPayload().getExpiration().before(new Date())) {
            throw new CommonErrorException(ErrorStatus.EXPIRED_TOKEN);
        }
    }

    public TokenDto validateRefreshToken(String refreshToken, String memberId) {
        String refreshTokenKey = REFRESH_TOKEN_KEY + refreshToken;
        String memberIdFromRefreshToken = String.valueOf(redisService.getFromRedis(refreshTokenKey));

        if (!memberId.equals(memberIdFromRefreshToken)) {
            throw new CommonErrorException(ErrorStatus.REFRESH_TOKEN_VERIFY_FAIL);
        }

        return getNewAccessTokenAndRefreshToken(memberId, refreshTokenKey);
    }

    public PublicKey getPublicKeyFromBase64Encrypted(String base64PublicKey) {
        try {
            byte[] decodedBase64PubKey = Base64.getDecoder().decode(base64PublicKey);

            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decodedBase64PubKey));
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.TOKEN_PARSE_ERROR);
        }
    }

    public PrivateKey getPrivateKeyFromBase64Encrypted(String base64PrivateKey) {
        try {
            byte[] decodedBase64PrivateKey = Base64.getDecoder().decode(base64PrivateKey);

            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decodedBase64PrivateKey));
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.TOKEN_PARSE_ERROR);
        }
    }

    private String createJti() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    private Map parsePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new CommonErrorException(ErrorStatus.TOKEN_PARSE_ERROR);
            }

            // payload 부분 Base64 디코딩
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.TOKEN_PARSE_ERROR);
        }
    }
}
