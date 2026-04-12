package com.example.jdkproject.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

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
}
