package com.example.hellomessagequeue.step9;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final StockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void sendMessage(StockEntity stockEntity, boolean testCase) {
        if(stockEntity.getUserId() == null || stockEntity.getUserId().isEmpty()) {
            throw new RuntimeException("user id is null");
        }

        stockEntity.prepareProcessed(LocalDateTime.now());
        StockEntity saved = stockRepository.save(stockEntity);

        System.out.println("[producer entity] : " + saved);

        try {
            // 메시지를 rabbitmq에 전송
            CorrelationData correlationData = new CorrelationData(saved.getId().toString());
            rabbitTemplate.convertAndSend(
                    testCase ? "nonExistentExchange" : RabbitMQConfig.EXCHANGE_NAME,
                    testCase ? "invalidRoutingKey" : RabbitMQConfig.ROUTING_KEY,
                    saved,
                    correlationData
            );

            if(correlationData.getFuture().get(5, TimeUnit.SECONDS).isAck()) {
                System.out.println("[producer entity] ack : " + saved);
            } else {
                throw new RuntimeException("# confirm 실패 - 롤백");
            }

        } catch (Exception e) {
            System.out.println("[producer exception fail] : " + e);
            throw new RuntimeException(e);
        }
    }
}
