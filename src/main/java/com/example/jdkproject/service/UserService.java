package com.example.jdkproject.service;

import com.example.jdkproject.dao.UserDao;
import com.example.jdkproject.domain.Member;
import com.example.jdkproject.domain.MemberSecureInfo;
import com.example.jdkproject.dto.UserDto;
import com.example.jdkproject.entity.MemberSecureVo;
import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberRepository;
import com.example.jdkproject.repository.MemberSecureRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {
    private final UserDao userDao;
    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private final MemberSecureRepository memberSecureRepository;

    public UserService(UserDao userDao, JwtTokenService jwtTokenService, MemberRepository memberRepository, MemberSecureRepository memberSecureRepository) {
        this.userDao = userDao;
        this.jwtTokenService = jwtTokenService;
        this.memberRepository = memberRepository;
        this.memberSecureRepository = memberSecureRepository;
    }

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public MemberVo checkId(String id) {
        MemberVo member = memberRepository.findUserByUserId(id);

        if (member == null) {
            return null;
        }

        return member;
    }

    @Transactional
    public void register(UserDto userDto) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, InvalidKeySpecException {
        // check user id
        MemberVo member = memberRepository.findUserByUserId(userDto.getUserId());
        if (member != null) {
            throw new CommonErrorException(ErrorStatus.ALREADY_EXIST);
        }

        //create user id
        String memberId = UUID.randomUUID().toString();

        //pw sha-256
        String userPw = encrypt(userDto.getUserPw());
        userDto.setUserPw(userPw);

        //email, phone rsa
        KeyPair keyPair = genRSAKeyPair();
        PublicKey publicKeyPair = keyPair.getPublic();

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        String encEmail, encPhoneNumber;

        if (StringUtils.isNotBlank(userDto.getEmail())) {
            encEmail = encryptRSA(userDto.getEmail(), publicKeyPair);
            userDto.setEmail(encEmail);
        }

        if (StringUtils.isNotBlank(userDto.getPhone())) {
            encPhoneNumber = encryptRSA(userDto.getPhone(), publicKeyPair);
            userDto.setPhone(encPhoneNumber);
        }

        //put user secure info jpa
        MemberSecureVo memberSecureVo = new MemberSecureVo(memberId, publicKey, privateKey);
        Object result = memberSecureRepository.save(memberSecureVo);
        if (result == null) {
            log.error("DB Insert Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

        userDto.setMemberId(memberId);

        log.info("Member: {}", userDto);

        // put member jpa
        MemberVo memberVo = new MemberVo(memberId, userDto.getUserId(), userPw, userDto.getName(), userDto.getEmail(), userDto.getPhone(), userDto.getNickName(), LocalDateTime.now(ZoneOffset.UTC).toString(), null);
        Object registerResult = memberRepository.save(memberVo);
        if (registerResult == null) {
            log.error("DB Insert Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    public Member login(String userId, String userPw) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException {
        MemberVo memberVo = memberRepository.findUserByUserId(userId);
        if (memberVo == null) {
            log.warn("User not Exist");
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        //VO to DTO
        Member member = new Member(memberVo.getMemberId(), userId, userPw, memberVo.getName(), memberVo.getEmail(), memberVo.getPhone(), memberVo.getNickname(), null);
        log.info("Member: {}", member.getMemberId());

        String encPw = encrypt(userPw);
        if (!encPw.equals(memberVo.getUserPw())) {
            throw new CommonErrorException(ErrorStatus.WRONG_USER_PASSWORD);
        }

        //get member secure
        MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(member.getMemberId());

        // email
        String encEmail = member.getEmail();
        String email = decryptRSA(encEmail, getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey()));
        member.setEmail(email);

        //phone number
        String encPhoneNumber = member.getPhone();
        if (encPhoneNumber != null) {
            String phoneNumber = decryptRSA(encPhoneNumber, getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey()));
            member.setPhone(phoneNumber);
        }

        //jwt token
        String token = jwtTokenService.createToken(member.getMemberId(), member.getName(), member.getEmail(), getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey()));
        member.setToken(token);

        //redis 등록
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(member.getMemberId(), token, 3600000, TimeUnit.MILLISECONDS);

        int loginTimeResult = userDao.updateLoginTime(member.getMemberId());
        if (loginTimeResult <= 0 ) {
            log.warn("Login Time Update DB Error!");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

        return member;
    }

    public void verifyToken(String memberId, String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String tokenFromRedis = valueOperations.get(memberId);

        if (!token.equals(tokenFromRedis)) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }

        MemberSecureInfo memberSecureInfo = userDao.getUserSecure(memberId);
        if (memberSecureInfo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        if (!jwtTokenService.validateToken(token, getPublicKeyFromBase64Encrypted(memberSecureInfo.getPublicKey()))) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }
    }

    private String encrypt(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());

        return bytesToHex(md.digest());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024, new SecureRandom());
        return gen.genKeyPair();
    }

    /**
     * Public Key로 RSA 암호화를 수행
     */
    public static String encryptRSA(String plainText, PublicKey publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] bytePlain = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(bytePlain);
    }

    /**
     * Private Key로 RSA 복호화를 수행
     */
    public static String decryptRSA(String encrypted, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] byteEncrypted = Base64.getDecoder().decode(encrypted.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytePlain = cipher.doFinal(byteEncrypted);
        return new String(bytePlain, "utf-8");
    }

    public static PublicKey getPublicKeyFromBase64Encrypted(String base64PublicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedBase64PubKey = Base64.getDecoder().decode(base64PublicKey);

        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decodedBase64PubKey));
    }

    public static PrivateKey getPrivateKeyFromBase64Encrypted(String base64PrivateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedBase64PrivateKey = Base64.getDecoder().decode(base64PrivateKey);

        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decodedBase64PrivateKey));
    }
}
