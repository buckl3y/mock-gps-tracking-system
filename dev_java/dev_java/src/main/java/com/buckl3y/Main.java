package com.buckl3y;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        GPSProducer producer = new GPSProducer();
        List<String> serialNumbers = producer.generateSerialNumbers(100);
        while (true) { 
            try {
                Thread.sleep(5000);
                producer.refreshCoordinates(serialNumbers);
                producer.sendMessages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
