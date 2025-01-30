package com.buckl3y;

import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        
        final String rabbitmqQueue = System.getenv("RABBITMQ_QUEUE_NAME");
        final String rabbitmqUser = System.getenv("RABBITMQ_DEFAULT_USER");
        final String rabbitmqPass = System.getenv("RABBITMQ_DEFAULT_PASS");
        final String rabbitmqHost = System.getenv("RABBITMQ_HOST");
        final int rabbitmqPort = Integer.parseInt(System.getenv("RABBITMQ_PORT"));

        GPSProducer producer = new GPSProducer(rabbitmqHost, rabbitmqPort, rabbitmqQueue, rabbitmqUser, rabbitmqPass);
        List<String> serialNumbers = producer.generateSerialNumbers(10);
        while (true) {
            try {
                Thread.sleep(5000);
                producer.generateCoordinates(serialNumbers);
                producer.sendMessages();
            } catch (InterruptedException e) {
                System.out.println("Error!");
            }
        }
    }
}
