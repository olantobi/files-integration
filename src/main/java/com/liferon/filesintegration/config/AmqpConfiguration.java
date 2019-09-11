package com.liferon.filesintegration.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    @Value("${spring.rabbitmq.exchange:ascii}")
    private String exchangeName;

    @Bean
    Exchange exchange() {
        return ExchangeBuilder.directExchange(this.exchangeName).durable(true).build();
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(this.exchangeName).build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(this.queue())
            .to(this.exchange())
            .with(this.exchangeName)
            .noargs();
    }
}
