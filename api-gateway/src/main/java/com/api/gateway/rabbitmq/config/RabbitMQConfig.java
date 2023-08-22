package com.api.gateway.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean("bootExchange")
    public Exchange bootExchange() {
        return ExchangeBuilder.topicExchange(com.api.common.config.RabbitMQConfig.EXCHANGE).durable(true).build();
    }

    @Bean("bootQueue")
    public Queue bootQueue() {
        return QueueBuilder.durable(com.api.common.config.RabbitMQConfig.ROUTING).build();
    }

    @Bean
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue queue, @Qualifier("bootExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(com.api.common.config.RabbitMQConfig.ROUTING).noargs();
    }
}
