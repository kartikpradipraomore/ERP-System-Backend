package com.storemate.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String to, String subject, String text, List<String> list) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("storemate8@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if (!list.isEmpty()) {
            message.setCc(getCcArray(list));
        }

        mailSender.send(message);

    }

    private String[] getCcArray(List<String> list) {
        String[] cc = new String[list.size()];
        for (int i = 0; i < cc.length; i++) {
            cc[i] = list.get(i);
        }
        return cc;
    }

    public void forgetMail(String to, String subject, String password) {
        try {
            log.info("Creating MIME message...");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            log.info("Setting email details...");
            helper.setFrom("storemate8@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlMsg = "<p><b>Your Login details for StoreMate</b><br><b>Email: </b>" + to + " <br><b>Password: </b>" + password + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
            message.setContent(htmlMsg, "text/html");

            log.info("Sending email to: {}", to);
            mailSender.send(message);
            log.info("Email sent successfully.");
        } catch (Exception e) {
            log.error("Error sending email", e);  // Log any exception
          e.printStackTrace(); // Rethrow exception so it is caught in forgetPassword
        }
    }




}
