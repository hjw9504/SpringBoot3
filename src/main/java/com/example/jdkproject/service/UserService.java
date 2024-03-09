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
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {
    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private final MemberSecureRepository memberSecureRepository;

    public UserService(JwtTokenService jwtTokenService, MemberRepository memberRepository, MemberSecureRepository memberSecureRepository) {
        this.jwtTokenService = jwtTokenService;
        this.memberRepository = memberRepository;
        this.memberSecureRepository = memberSecureRepository;
    }

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public List<Member> checkId(String id, String memberId) {
        try {
            List<Member> memberList = new ArrayList<>();

            if ("ALL".equals(id)) {
                List<MemberVo> members = memberRepository.findAllUser();
                for (MemberVo memberVo : members) {
                    //get member secure
                    MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberVo.getMemberId());

                    // email
                    String encEmail = memberVo.getEmail();
                    String email = decryptRSA(encEmail, getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey()));
                    Member member = new Member(memberVo.getUserId(), memberVo.getName(), email, memberVo.getPhone(), memberVo.getNickname(), memberVo.getRegisterTime(), memberVo.getRecentLoginTime(), memberVo.getRole());
                    memberList.add(member);
                }
                return memberList;
            }

            List<MemberVo> memberResult = memberRepository.findUserByMemberId(memberId);
            if (memberResult == null && memberResult.size() == 0) {
                return null;
            }

            MemberVo memberVo = memberResult.get(0);
            MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberVo.getMemberId());

            // email
            String encEmail = memberVo.getEmail();
            String email = decryptRSA(encEmail, getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey()));
            Member member = new Member(memberVo.getUserId(), memberVo.getName(), email, memberVo.getPhone(), memberVo.getNickname(), memberVo.getRegisterTime(), memberVo.getRecentLoginTime(), memberVo.getRole());
            memberList.add(member);
            return memberList;
        } catch(Exception e) {
            return null;
        }
    }

    @Transactional
    public void register(UserDto userDto) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, InvalidKeySpecException {
        // check user id
        Boolean isExistingUser = checkExistPlayer(userDto.getUserId());
        if (isExistingUser) {
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
        MemberSecureVo memberSecureVo = new MemberSecureVo(memberId, privateKey, publicKey);
        Object result = memberSecureRepository.save(memberSecureVo);
        if (result == null) {
            log.error("DB Insert Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

        userDto.setMemberId(memberId);

        log.info("Member: {}", userDto);

        // put member jpa
        MemberVo memberVo = new MemberVo(memberId, userDto.getUserId(), userPw, userDto.getName(), userDto.getEmail(), userDto.getPhone(), userDto.getNickName(), LocalDateTime.now(ZoneOffset.UTC).toString(), null, "USER");
        Object registerResult = memberRepository.save(memberVo);
        if (registerResult == null) {
            log.error("DB Insert Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    public Boolean checkExistPlayer(String userId) {
        MemberVo member = memberRepository.findUserByUserId(userId);
        if (member != null) {
            return true;
        }

        return false;
    }

    public Member login(String userId, String userPw) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException {
        MemberVo memberVo = memberRepository.findUserByUserId(userId);
        if (memberVo == null) {
            log.warn("User not Exist");
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        //VO to DTO
        Member member = new Member(memberVo.getMemberId(), userId, userPw, null, memberVo.getName(), memberVo.getEmail(), memberVo.getPhone(), memberVo.getNickname(), null, null, null, memberVo.getRole());
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

        int loginTimeResult = memberRepository.updateLastLoginTime(member.getMemberId(), LocalDateTime.now(ZoneOffset.UTC).toString());
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

        MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberId);
        if (memberSecureInfo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        if (!jwtTokenService.validateToken(token, getPublicKeyFromBase64Encrypted(memberSecureInfo.getPublicKey()))) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }
    }

    public void resetPassword(String userId, String userPassword) {
        try {
            String userPw = encrypt(userPassword);
            int result = memberRepository.resetUserPassword(userId, userPw);
            return;
        } catch(Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    public void updateNickName(Member member) {
        MemberVo memberVo = memberRepository.findUserByUserId(member.getUserId());
        if (memberVo == null) {
            log.warn("User not Exist");
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        memberRepository.updateNickName(member.getMemberId(), member.getNickName());
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
