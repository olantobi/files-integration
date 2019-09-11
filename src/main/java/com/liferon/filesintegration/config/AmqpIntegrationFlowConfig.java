package com.liferon.filesintegration.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Configuration
@RequiredArgsConstructor
public class AmqpIntegrationFlowConfig {

    private final MessageChannel asciiProcessor;

    @Value("${spring.rabbitmq.exchange:ascii}")
    private String exchangeName;

    @Bean
    IntegrationFlow amqp(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(this.asciiProcessor)
            .handle(Amqp.outboundAdapter(amqpTemplate)
                   .exchangeName(this.exchangeName)
                   .routingKey(this.exchangeName))
            .get();
    }
}
