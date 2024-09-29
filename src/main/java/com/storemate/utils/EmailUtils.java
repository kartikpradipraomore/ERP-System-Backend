package com.storemate.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String to, String subject, String text, List<String> list) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kartikmore441@gmail.com");
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
}
