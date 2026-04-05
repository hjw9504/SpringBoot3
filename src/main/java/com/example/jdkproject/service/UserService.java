package com.example.jdkproject.service;

import com.example.jdkproject.domain.Member;
import com.example.jdkproject.dto.IDPLoginDto;
import com.example.jdkproject.dto.IdpUser;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.dto.UserDto;
import com.example.jdkproject.entity.MemberChannelVo;
import com.example.jdkproject.entity.MemberSecureVo;
import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberChannelRepository;
import com.example.jdkproject.repository.MemberRepository;
import com.example.jdkproject.repository.MemberSecureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final JwtTokenService jwtTokenService;
    private final OAuthService oAuthService;
    private final MemberRepository memberRepository;
    private final MemberSecureRepository memberSecureRepository;
    private final MemberChannelRepository memberChannelRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private final static String PROFILE_IMAGE_PREFIX = "https://api.dicebear.com/7.x/lorelei/svg?seed=";

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
                    Member member = new Member(memberVo.getUserId(), memberVo.getName(), email, memberVo.getPhone(), memberVo.getNickname(), memberVo.getRegisterTime(), memberVo.getRecentLoginTime(), memberVo.getRole(), memberVo.getUpdateNicknameTime(), memberVo.getProfileImage());
                    memberList.add(member);
                }
                return memberList;
            }

            MemberVo memberVo = memberRepository.findUserByMemberId(memberId)
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0))
                    .orElseThrow(() -> new CommonErrorException(ErrorStatus.NOT_FOUND));

            MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberVo.getMemberId());

            // email
            String encEmail = memberVo.getEmail();
            String email = StringUtils.isNotBlank(encEmail) ? decryptRSA(encEmail, getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey())) : null;
            Member member = new Member(memberVo.getUserId(), memberVo.getName(), email, memberVo.getPhone(), memberVo.getNickname(), memberVo.getRegisterTime(), memberVo.getRecentLoginTime(), memberVo.getRole(), memberVo.getUpdateNicknameTime(), memberVo.getProfileImage());
            memberList.add(member);
            return memberList;
        } catch(Exception e) {
            log.warn("Error while getting user info : ", e);
            return null;
        }
    }

    @Transactional
    public void register(UserDto userDto) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
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
        memberSecureRepository.save(memberSecureVo);

        userDto.setMemberId(memberId);

        log.info("Member: {}", userDto);

        // put member jpa
        MemberVo memberVo = MemberVo.builder()
                .memberId(memberId)
                .userId(userDto.getUserId())
                .userPw(userPw)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .nickname(userDto.getNickName())
                .registerTime(LocalDateTime.now())
                .recentLoginTime(null)
                .role("USER")
                .profileImage(PROFILE_IMAGE_PREFIX + RandomStringUtils.randomAlphanumeric(10))
                .updateNicknameTime(null)
                .build();

        memberRepository.save(memberVo);
    }

    @Transactional
    public void registerIdp(IDPLoginDto dto) {
        try {
            // get user channel
            IdpUser idpUser = oAuthService.verifyIDPToken(dto.getAccessToken(), dto.getIdpType());

            Optional<MemberChannelVo> idpRegisterResult = memberChannelRepository.findUserByIdpUserIdAndIdpType(idpUser.getIdpUserId(), idpUser.getIdpType());
            if (idpRegisterResult.isPresent()) {
                throw new CommonErrorException(ErrorStatus.ALREADY_EXIST);
            }

            //create user id
            String memberId = UUID.randomUUID().toString();

            // put member jpa
            String idpUserName = idpUser.getIdpType() + RandomStringUtils.randomNumeric(10);

            MemberVo memberVo = MemberVo.builder()
                    .memberId(memberId)
                    .userId(idpUserName)
                    .userPw(UUID.randomUUID().toString())
                    .name(idpUserName)
                    .email(null)
                    .phone(null)
                    .nickname(idpUser.getIdpType().toUpperCase() + " USER")
                    .registerTime(LocalDateTime.now())
                    .recentLoginTime(null)
                    .role("USER")
                    .profileImage(PROFILE_IMAGE_PREFIX + RandomStringUtils.randomAlphanumeric(10))
                    .updateNicknameTime(null)
                    .build();

            memberRepository.save(memberVo);

            KeyPair keyPair = genRSAKeyPair();
            PublicKey publicKeyPair = keyPair.getPublic();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            //put user secure info jpa
            MemberSecureVo memberSecureVo = new MemberSecureVo(memberId, privateKey, publicKey);
            memberSecureRepository.save(memberSecureVo);

            MemberChannelVo memberChannelVo = MemberChannelVo.builder()
                    .memberId(memberId)
                    .idpUserId(idpUser.getIdpUserId())
                    .idpType(idpUser.getIdpType())
                    .registerTime(LocalDateTime.now())
                    .build();

            memberChannelRepository.save(memberChannelVo);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    public Boolean checkExistPlayer(String userId) {
        MemberVo member = memberRepository.findUserByUserId(userId);
        return member != null;
    }

    public Member login(String userId, String userPw) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException {
        MemberVo memberVo = memberRepository.findUserByUserId(userId);
        if (memberVo == null) {
            log.warn("User not Exist");
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        //VO to DTO
        Member member = new Member().toMember(memberVo, userId);
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
        member.setAccessToken(token);

        memberVo.updateMemberLastLoginTime();
        memberRepository.save(memberVo);

        return member;
    }

    public Member loginIdp(String idpUserId, String idpType) {
        Optional<MemberChannelVo> memberChannelResult = memberChannelRepository.findUserByIdpUserIdAndIdpType(idpUserId, idpType);
        if (memberChannelResult.isEmpty()) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        MemberChannelVo memberChannelVo = memberChannelResult.get();

        // get member with member_id
        MemberVo memberVo = memberRepository.findUserByMemberId(memberChannelVo.getMemberId())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElseThrow(() -> new CommonErrorException(ErrorStatus.NOT_FOUND));

        //VO to DTO
        Member member = new Member().toMember(memberVo, memberVo.getUserId());

        //get member secure
        MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(member.getMemberId());

        //jwt token
        String token = null;
        try {
            token = jwtTokenService.createToken(member.getMemberId(), member.getName(), member.getEmail(), getPrivateKeyFromBase64Encrypted(memberSecureInfo.getPrivateKey()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

        member.setAccessToken(token);

        memberVo.updateMemberLastLoginTime();
        memberRepository.save(memberVo);

        return member;
    }

    public JtiInfo verifyToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // token payload parse
        Map<String, String> claims = parsePayload(token);

        // get jti and info from redis
        JtiInfo jtiInfo;
        try {
            String jti = claims.get("jti");
            jtiInfo = objectMapper.readValue(getRedisData(jti), JtiInfo.class);
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }

        String memberId = jtiInfo.getMemberId();

        MemberSecureVo memberSecureInfo = memberSecureRepository.findInfoByMemberId(memberId);
        if (memberSecureInfo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        if (!jwtTokenService.validateToken(token, getPublicKeyFromBase64Encrypted(memberSecureInfo.getPublicKey()))) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }

        return jtiInfo;
    }

    public void resetPassword(String userId, String userPassword) {
        try {
            String userPw = encrypt(userPassword);

            // 기존 password를 가져와서 동일한지 비교
            MemberVo memberVo = memberRepository.findUserByUserId(userId);
            if (memberVo == null) {
                log.warn("User not Exist");
                throw new CommonErrorException(ErrorStatus.NOT_FOUND);
            }

            if (userPw.equals(memberVo.getUserPw())) {
                throw new CommonErrorException(ErrorStatus.SAME_PASSWORD);
            }

            int result = memberRepository.resetUserPassword(userId, userPw);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    public void updateNickName(Member member) {
        MemberVo memberVo = memberRepository.findUserByUserId(member.getUserId());
        if (memberVo == null) {
            log.warn("User not Exist");
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        // 최근 업데이트 확인
        if (memberVo.getUpdateNicknameTime() != null && LocalDateTime.now().minusDays(1).isBefore(memberVo.getUpdateNicknameTime())) {
            log.info("{} {}", LocalDateTime.now().minusDays(1), memberVo.getUpdateNicknameTime());
            throw new CommonErrorException(ErrorStatus.CANNOT_UPDATE_NICKNAME);
        }

        memberVo.updateMemberNickname(member.getNickname());

        memberRepository.save(memberVo);
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
        gen.initialize(2048, new SecureRandom());
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

    private String getRedisData(String key) {
        return redisTemplate.opsForValue().get(key);
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
