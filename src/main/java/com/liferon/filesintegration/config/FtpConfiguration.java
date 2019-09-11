package com.liferon.filesintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

@Configuration
public class FtpConfiguration {
    @Bean
    DefaultFtpSessionFactory ftpFileSessionFactory(
        @Value("${ftp.host:localhost}") String host,
        @Value("${ftp.port:21}") int port,
        @Value("${ftp.username:ftpusername}") String username,
        @Value("${ftp.password:ftppassword}") String password) {
        DefaultFtpSessionFactory ftpSessionFactory = new DefaultFtpSessionFactory();
        ftpSessionFactory.setHost(host);
        ftpSessionFactory.setPort(port);
        ftpSessionFactory.setPassword(password);
        ftpSessionFactory.setUsername(username);
        ftpSessionFactory.setClientMode(2);     // Passive Mode

        return ftpSessionFactory;
    }
}
