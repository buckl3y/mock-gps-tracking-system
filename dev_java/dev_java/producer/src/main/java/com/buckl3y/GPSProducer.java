package com.buckl3y;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class GPSProducer {

    List<Message> messages = new ArrayList<>();
    private final String library = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String rabbitmqQueue;
    private final String rabbitmqUser;
    private final String rabbitmqPass;
    private final String rabbitmqHost;
    private final int rabbitmqPort;

    public GPSProducer(String host, int port, String queue, String user, String pass) {
        this.rabbitmqQueue = queue;
        this.rabbitmqUser = user;
        this.rabbitmqPass = pass;
        this.rabbitmqHost = host;
        this.rabbitmqPort = port;
  
    }

    private Connection conn = null;
    private Channel chnl = null;
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

    public void generateCoordinates(List<String> list) {
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
        RabbitMQManager rabbitMQManager = new RabbitMQManager(rabbitmqHost, rabbitmqPort, rabbitmqQueue, rabbitmqUser, rabbitmqPass);
        if (!messages.isEmpty()) { // first check if there are messages to send 
            if(conn != null && chnl != null) { // then ensure there is a connection before sending messages
                for (Message message : messages) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("serialNumber", message.getSerialNumber());
                        json.put("time", message.getTime());
                        json.put("latitude", message.getLatitude());
                        json.put("longitude", message.getLongitude());
                        String JSONMessage = json.toString();
                        chnl.basicPublish("", rabbitmqQueue, null, JSONMessage.getBytes());
                    } catch (IOException | JSONException e) {
                        System.out.println("Error!");
                    }
                }
            } else {
                rabbitMQManager.connectRabbitMQ(); // if this fails, method returns and is rerun by a driver class every ~5 seconds.
                conn = rabbitMQManager.getConnection();
                chnl = rabbitMQManager.getChannel();
            }
            System.out.println("Messages sent successfully");
        } else {
            System.out.println("No messages in list! please generate some messages.");
        }
    }
}
