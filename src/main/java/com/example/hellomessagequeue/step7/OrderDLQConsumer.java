package com.example.hellomessagequeue.step7;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OrderDLQConsumer {
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.DLQ)
    public void process(String message, Channel channel, @Header("amqp_deliveryTag") long tag) {
        System.out.println("DLQ Message Received: " + message);

        try {
            String fixMessage = "success";

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    "order.completed.shipping",
                    fixMessage
            );
            System.out.println("DLQ Message Sent: " + fixMessage);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            System.err.println("### [DLQ Consumer Error] " + e.getMessage());
            try {
                channel.basicReject(tag, true);
            } catch (IOException e1) {
                System.err.println("# fail & reject message : " + e1.getMessage());
            }
        }
    }
}
