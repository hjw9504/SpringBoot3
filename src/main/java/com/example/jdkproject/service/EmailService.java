package com.example.jdkproject.service;

import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisService redisService;

    private final long emailTokenTtl = 60 * 60 * 1000L;
    private final long emailAuthCodeTtl = 10 * 60 * 1000L;

    public void sendRegisterEmail(String to, String name, String userId, String email, LocalDateTime registerTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[JUNGS] 회원가입을 축하합니다!");
            helper.setFrom("hjw95041@gmail.com");
            helper.setText(buildEmailContent(name, userId, email, registerTime), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 발송 실패 - to: {}, error: {}", to, e.getMessage());
        }
    }

    public String getAuthEmail(String email) {

        try {
            String emailAuthKey = "EMAIL_TOKEN:"+email;
            String emailToken = RandomStringUtils.randomAlphanumeric(20);

            String authCode = RandomStringUtils.randomNumeric(5);

            // 이메일 1시간 5개 초과했는지 체크
            List<String> emailTokenList = redisService.getListFromRedis(emailAuthKey);
            if (emailTokenList.size() >= 5) {
                throw new CommonErrorException(ErrorStatus.EXCEEDED_EMAIL_TOKEN);
            }

            redisService.saveListToRedis(emailAuthKey, emailToken, emailTokenTtl);
            redisService.saveToRedis(emailToken, authCode, emailAuthCodeTtl);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[JUNGS] 이메일 인증을 진행해주세요!");
            helper.setFrom("hjw95041@gmail.com");
            helper.setText(buildAuthCodeEmailContent(authCode), true);

            mailSender.send(message);

            return emailToken;
        } catch (MessagingException e) {
            log.error("이메일 발송 실패 - to: {}, error: {}", email, e.getMessage());
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    public void checkAuthCode(String email, String emailToken, String authCode) {
        String emailAuthKey = "EMAIL_TOKEN:"+email;
        List<String> emailTokenList = redisService.getListFromRedis(emailAuthKey);
        if (emailTokenList.isEmpty() || !emailTokenList.contains(emailToken)) {
            throw new CommonErrorException(ErrorStatus.EMAIL_TOKEN_VERIFY_FAIL);
        }

        String authCodeFromRedis = (String) redisService.getFromRedis(emailToken);
        if (StringUtils.isBlank(authCodeFromRedis) || !authCodeFromRedis.equals(authCode)) {
            throw new CommonErrorException(ErrorStatus.EMAIL_TOKEN_VERIFY_FAIL);
        }
    }

    private String buildEmailContent(String name, String userId, String email, LocalDateTime registerTime) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("userId", userId);
        context.setVariable("email", email);
        context.setVariable("registerTime",
                registerTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm")));
        context.setVariable("siteUrl", "http://54.180.225.237");

        return templateEngine.process("register", context);
    }

    private String buildAuthCodeEmailContent(String authCode) {
        Context context = new Context();
        context.setVariable("authCode", authCode);
        context.setVariable("siteUrl", "http://54.180.225.237");

        return templateEngine.process("email-auth", context);
    }
}
