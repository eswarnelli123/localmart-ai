package com.localmart.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("LocalMart AI Email Verification");
        message.setText("Your OTP is: " + otp + "\n\nUse it to verify your account.");
        try {
            mailSender.send(message);
            log.info("OTP email sent successfully to {}", to);
        } catch (MailException ex) {
            log.error("Failed to send OTP email to {}. OTP: {}", to, otp, ex);
            throw ex;
        }
    }

    public void sendPasswordResetEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("LocalMart AI Password Reset");
        message.setText("Your password reset OTP is: " + otp + "\n\nUse it to reset your password.");
        try {
            mailSender.send(message);
            log.info("Password reset email sent successfully to {}", to);
        } catch (MailException ex) {
            log.error("Failed to send password reset email to {}. OTP: {}", to, otp, ex);
            throw ex;
        }
    }
}
