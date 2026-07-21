package com.localmart.config;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(prefix = "spring.mail", name = "host")
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(mailProperties.getProperties().getOrDefault("mail.smtp.auth", "true")));
        props.put("mail.smtp.starttls.enable", String.valueOf(mailProperties.getProperties().getOrDefault("mail.smtp.starttls.enable", "true")));
        props.put("mail.smtp.starttls.required", String.valueOf(mailProperties.getProperties().getOrDefault("mail.smtp.starttls.required", "true")));
        props.put("mail.debug", mailProperties.getProperties().getOrDefault("mail.debug", "false"));
        props.putAll(mailProperties.getProperties());

        return mailSender;
    }
}
