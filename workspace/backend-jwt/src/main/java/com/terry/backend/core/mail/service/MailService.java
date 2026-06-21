package com.terry.backend.core.mail.service;

import com.terry.backend.core.mail.dto.MailDTO;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class MailService {

    /**
    private final JavaMailSender mailSender;
    private final String fromMail;
    private final String formName;

    public MailService(
            JavaMailSender mailSender
            , @Value("${mail.fromMail}") String fromMail
            , @Value("${mail.fromName}") String formName
    ) {
        this.mailSender = mailSender;
        this.fromMail = fromMail;
        this.formName = formName;
    }

    public void sendMail(MailDTO params) throws UnsupportedEncodingException {

        log.info("======================================================== 메일 전송  ========================================");

        log.info("from email : {}", fromMail);
        log.info("to email : {}", params.getToMail());
        log.info("to name : {}", params.getToName());
        log.info("subject : {}", params.getSubject());


        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");



            helper.setFrom(new InternetAddress(fromMail, formName));
            helper.setTo(new InternetAddress(params.getToMail(), params.getToName()));
            helper.setSubject(params.getSubject());
            helper.setText(params.getContent(), true);

            mailSender.send(message);


            log.info("Mail Send Success");

        } catch (Exception e) {
            log.info("Mail Send Fail");
            throw new RuntimeException("Mail Send Fail");

        }
    }*/


}
