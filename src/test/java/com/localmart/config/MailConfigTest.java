package com.localmart.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MailConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MailConfig.class);

    @Test
    void shouldCreateJavaMailSenderBeanWhenMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JavaMailSender.class);
            assertThat(context.getBean(JavaMailSender.class)).isNotNull();
        });
    }

    @Test
    void shouldUseConfiguredMailProperties() {
        contextRunner.withPropertyValues(
                "spring.mail.host=smtp.example.com",
                "spring.mail.port=2526",
                "spring.mail.username=mailer@example.com",
                "spring.mail.password=secret",
                "spring.mail.properties.mail.smtp.auth=true",
                "spring.mail.properties.mail.smtp.starttls.enable=true",
                "spring.mail.from=alerts@example.com"
        ).run(context -> {
            assertThat(context).hasSingleBean(JavaMailSender.class);
            JavaMailSender senderBean = context.getBean(JavaMailSender.class);
            JavaMailSenderImpl sender = (JavaMailSenderImpl) senderBean;
            assertThat(sender.getHost()).isEqualTo("smtp.example.com");
            assertThat(sender.getPort()).isEqualTo(2526);
            assertThat(sender.getUsername()).isEqualTo("mailer@example.com");
            assertThat(sender.getPassword()).isEqualTo("secret");
            assertThat(sender.getJavaMailProperties().getProperty("mail.smtp.auth")).isEqualTo("true");
            assertThat(sender.getJavaMailProperties().getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
            assertThat(context.getEnvironment().getProperty("spring.mail.from")).isEqualTo("alerts@example.com");
        });
    }
}
