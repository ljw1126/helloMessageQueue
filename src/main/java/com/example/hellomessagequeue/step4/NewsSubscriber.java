package com.example.hellomessagequeue.step4;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsSubscriber {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.JAVA_QUEUE)
    public void javaNews(String message) {
        simpMessagingTemplate.convertAndSend("/topic/java", message); // 뉴스 브로드캐스트
    }

    @RabbitListener(queues = RabbitMQConfig.SPRING_QUEUE)
    public void springNews(String message) {
        simpMessagingTemplate.convertAndSend("/topic/spring", message); // 기술 뉴스 브로드캐스트
    }

    @RabbitListener(queues = RabbitMQConfig.VUE_QUEUE)
    public void vueNews(String message) {
        simpMessagingTemplate.convertAndSend("/topic/vue", message); // 일반 뉴스 브로드캐스트
    }
}
