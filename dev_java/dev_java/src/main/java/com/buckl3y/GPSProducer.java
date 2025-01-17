package com.buckl3y;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class GPSProducer {
    List<Message> messages = new ArrayList<>();
    String library = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random rand = new Random();

    public List<String> generateSerialNumbers(int snCount) {
        int number = 1;
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= snCount; i++) {
            StringBuilder ranString = new StringBuilder();
            // generate random 5-byte string
            for (int j = 0; j < 5; j++) {
                int index = rand.nextInt(library.length());
                ranString.append(library.charAt(index));
            }
            String formattedNumber = String.format("%05d", number);
            String str = ranString + formattedNumber;
            list.add(str);
            number++;
        }
        return list;
    }

    public void refreshCoordinates(List<String> list) {
        ZonedDateTime time = ZonedDateTime.now();
        for (String sn : list) {
            Double randLongitude = rand.nextDouble() * 180;
            Double randLatitude = rand.nextDouble() * 180;
            randLongitude = (double) Math.round(randLongitude * 10000000) / 10000000;
            randLatitude = (double) Math.round(randLatitude * 10000000) / 10000000;
            messages.add(new Message(sn, time, randLatitude, randLongitude));
        }
    }

    public void sendMessages() {
        // send JSON messages to RabbitMQ
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                JSONObject json = new JSONObject();
                try {
                    json.put("serialNumber", message.getSerialNumber());
                    json.put("time", message.getTime());
                    json.put("latitude", message.getLatitude());
                    json.put("longitude", message.getLongitude());
                    System.out.println(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
