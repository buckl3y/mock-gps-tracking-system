package com.buckl3y;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        GPSProducer producer = new GPSProducer();
        List<String> serialNumbers = producer.generateSerialNumbers(10);
        producer.connectRabbitMQ();
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
