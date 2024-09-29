package com.storemate.utils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailTest {
    public static void main(String[] args) {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        ((JavaMailSenderImpl) mailSender).setHost("smtp.gmail.com");
        ((JavaMailSenderImpl) mailSender).setPort(587);
        ((JavaMailSenderImpl) mailSender).setUsername("storemate8@gmail.com");
        ((JavaMailSenderImpl) mailSender).setPassword("kartik@2000");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("storemate8@gmail.com");
        message.setTo("kartikore441@gmail.com");
        message.setSubject("Test Email");
        message.setText("This is a test email.");

        mailSender.send(message);
        System.out.println("Email sent successfully!");
    }
}
