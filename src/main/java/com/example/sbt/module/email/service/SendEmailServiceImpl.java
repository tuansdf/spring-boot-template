package com.example.sbt.module.email.service;

import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.module.email.dto.SendEmailRequest;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class SendEmailServiceImpl implements SendEmailService {
    private final JavaMailSender mailSender;
    private final ApplicationProperties applicationProperties;

    @Override
    public void send(SendEmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.getToEmail());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), ConversionUtils.safeToBoolean(request.getIsHtml()));
            helper.setFrom(applicationProperties.getMailFrom(), applicationProperties.getMailFromName());

            mailSender.send(message);
        } catch (Exception e) {
            log.error("SendEmailService.send ", e);
        }
    }

    @Async
    @Override
    public void sendAsync(SendEmailRequest request) {
        send(request);
    }
}
