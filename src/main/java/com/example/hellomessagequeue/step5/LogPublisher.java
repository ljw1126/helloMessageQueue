package com.example.hellomessagequeue.step5;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(String routingKey, String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, routingKey, message);
        System.out.println("message published :" + routingKey + ":" + message);
    }
}
