package com.example.hellomessagequeue.step9;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class TransactionController {
    private final MessageProducer messageProducer;

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody StockEntity stockEntity,
                                              @RequestParam boolean testCase) {
        System.out.println("Publisher Confirms Send message : " + stockEntity);

        try {
            messageProducer.sendMessage(stockEntity, testCase);
            return ResponseEntity.ok("Publisher Confirms sent successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Publisher Confirms 트랜잭션 실패 : " + e.getMessage());
        }
    }
}
