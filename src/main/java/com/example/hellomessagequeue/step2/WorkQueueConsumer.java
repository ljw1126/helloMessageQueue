package com.example.hellomessagequeue.step2;

import org.springframework.stereotype.Component;

@Component
public class WorkQueueConsumer {

    public void workQueueTask(String message) {
        String[] tokens = message.split("\\|");
        String originMessage = tokens[0];
        int duration = Integer.parseInt(tokens[1].trim());

        System.out.println("# Consumer Received: " + originMessage + " (duration: " + duration +" ms)");

        try {
        int seconds = duration / 1000; // 초 단위 환산
            for(int i = 0; i <= seconds; i++) {
                Thread.sleep(1000);
                System.out.print(".");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("\n[X] " + originMessage + " Completed!");
    }
}
