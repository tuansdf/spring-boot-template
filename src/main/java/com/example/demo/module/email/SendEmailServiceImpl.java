package com.example.demo.module.email;

import com.example.demo.common.util.ConversionUtils;
import com.example.demo.module.email.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        helper.setText(request.getBody(), ConversionUtils.safeToBool(request.getIsHtml()));

        mailSender.send(message);
    }

}
