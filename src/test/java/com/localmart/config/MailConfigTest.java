package com.localmart.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;

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
}
