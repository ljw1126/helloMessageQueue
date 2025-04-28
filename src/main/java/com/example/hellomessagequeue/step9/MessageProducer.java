package com.example.hellomessagequeue.step9;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final StockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void sendMessage(StockEntity stockEntity, String testCase) {
        rabbitTemplate.execute(channel -> {
            try {
                channel.txSelect();
                stockEntity.prepareProcessed(LocalDateTime.now());

                StockEntity saved = stockRepository.save(stockEntity);

                System.out.println("Stock Saved: " + saved);

                // 메시지 발행
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, saved);

                if("fail".equalsIgnoreCase(testCase)) {
                    throw new RuntimeException("트랜잭션 작업 중에 에러 발생");
                }

                channel.txCommit();
                System.out.println("트랜잭션이 정상적으로 처리 되었음!~");
            } catch (Exception e) {
                System.out.println("트랜잭션 실패 : " + e.getMessage());
                channel.txRollback();
                throw new RuntimeException("트랜잭션 롤백 완료 ", e);
            }   finally {
                if(channel != null) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        });
    }
}
