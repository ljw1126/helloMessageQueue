package com.example.hellomessagequeue.step2;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendWorkQueue(String workQueueMessage, int duration) {
        String message = workQueueMessage.concat("|" + duration);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        System.out.println("Sent work-queue :" + message);
    }
}
