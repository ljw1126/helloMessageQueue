package com.example.hellomessagequeue.step9;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MessageConsumer {
    private final StockRepository stockRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receiveTransaction(StockEntity stock, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
       try {
           System.out.println("[Consumer] " + stock);
           Thread.sleep(2000);

           StockEntity stockEntity = stockRepository.findById(stock.getId())
                   .orElseThrow(() -> new RuntimeException("Stock not found"));

           stockEntity.updatedProcessed(LocalDateTime.now());
           stockRepository.save(stockEntity);

           System.out.println("[Save Entity Consumer] " + stockEntity);
           channel.basicAck(deliveryTag, false);
       } catch (Exception e) {
           System.out.println("[Consumer Error] " + e.getMessage());
           try {
               channel.basicNack(deliveryTag, false, false);
           } catch (IOException ex) {
               System.out.println("[Consumer send nack] " + ex.getMessage());
               //throw new RuntimeException(ex);
           }
       }
    }
}
