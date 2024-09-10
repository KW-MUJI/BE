package com.muji_backend.kw_muji.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.transport.protocol}")
    private String protocol;

    @Value("${spring.mail.properties.smtp.auth}")
    private Boolean auth;

    @Value("${spring.mail.properties.smtp.socketFactory.class}")
    private String socketFactory;

    @Value("${spring.mail.properties.smtp.starttls.enable}")
    private String starttlsEnable;

    @Value("${spring.mail.properties.debug}")
    private String debug;

    @Value("${spring.mail.properties.trust}")
    private String trust;

    @Value("${spring.mail.properties.protocols}")
    private String protocols;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        mailSender.setJavaMailProperties(getProperties());

        return mailSender;
    }

    private Properties getProperties() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", protocol);
        javaMailProperties.put("mail.smtp.auth", auth);
        javaMailProperties.put("mail.smtp.socketFactory.class", socketFactory);
        javaMailProperties.put("mail.smtp.starttls.enable", starttlsEnable);
        javaMailProperties.put("mail.debug", debug);
        javaMailProperties.put("mail.smtp.ssl.trust", trust);
        javaMailProperties.put("mail.smtp.ssl.protocols", protocols);

        return javaMailProperties;
    }

}
