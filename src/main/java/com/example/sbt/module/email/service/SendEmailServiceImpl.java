package com.example.sbt.module.email.service;

import com.example.sbt.module.email.dto.SendEmailRequest;
import com.example.sbt.shared.util.ConversionUtils;
import jakarta.mail.MessagingException;
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

    @Override
    public void send(SendEmailRequest request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(request.getToEmail());
        helper.setSubject(request.getSubject());
        helper.setText(request.getBody(), ConversionUtils.safeToBoolean(request.getIsHtml()));

        mailSender.send(message);
    }

    @Async
    @Override
    public void sendAsync(SendEmailRequest request) throws MessagingException {
        send(request);
    }
}
