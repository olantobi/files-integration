package com.liferon.filesintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

@Configuration
public class FtpIntegrationConfig {

    @Value("${remote-directory:uploads}")
    private String remoteDirectory;

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

    @Bean
    IntegrationFlow files(@Value("${input-directory:${HOME}/Desktop/in}") File in, Environment environment,
                                  DefaultFtpSessionFactory ftpSessionFactory) {

        GenericTransformer<File, Message<String>> fileStringGenericTransformer = (File source) -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream printStream = new PrintStream(baos)) {
                ImageBanner imageBanner = new ImageBanner(new FileSystemResource(source));
                imageBanner.printBanner(environment, getClass(), printStream);


                return MessageBuilder.withPayload(new String(baos.toByteArray()))
                        .setHeader(FileHeaders.FILENAME, source.getAbsoluteFile().getName())
                        .build();
            } catch (IOException e) {
                ReflectionUtils.rethrowRuntimeException(e);
            }
            return null;
        };

        return IntegrationFlows
            .from(Files.inboundAdapter(in).autoCreateDirectory(true).preventDuplicates(true).patternFilter("*.jpg"),
                  poller -> poller.poller(pm -> pm.fixedRate(1000)))
            .transform(File.class, fileStringGenericTransformer)
            .handle(Ftp.outboundAdapter(ftpSessionFactory)
                       .remoteDirectory(remoteDirectory)
                       .fileNameGenerator(message -> {
                           Object o = message.getHeaders().get(FileHeaders.FILENAME);
                           String fileName = String.class.cast(o);
                           return fileName.split("\\.")[0]+".txt";
                       })).get();
    }
}
