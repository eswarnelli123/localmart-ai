package com.localmart.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("LocalMart AI Email Verification");
        message.setText("Your OTP is: " + otp + "\n\nUse it to verify your account.");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            System.err.println("[LocalMart AI] Failed to send OTP email to " + to + ". OTP: " + otp);
            ex.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("LocalMart AI Password Reset");
        message.setText("Your password reset OTP is: " + otp + "\n\nUse it to reset your password.");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            System.err.println("[LocalMart AI] Failed to send password reset email to " + to + ". OTP: " + otp);
            ex.printStackTrace();
        }
    }
}
