package com.buckl3y;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class GPSProducer {
    List<Message> messages = new ArrayList<>();
    private final String library = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String rabbitmqQueue = System.getenv("RABBITMQ_QUEUE_NAME");
    private final String rabbitmqUser = System.getenv("RABBITMQ_DEFAULT_USER");
    private final String rabbitmqPass = System.getenv("RABBITMQ_DEFAULT_PASS");
    private final String rabbitmqHost = System.getenv("RABBITMQ_HOST");
    private final String rabbitmqPort = System.getenv("RABBITMQ_PORT");
    private Connection conn;
    private Channel chnl;
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
                System.out.println("Messages sent successfully");
            } else {
                connectRabbitMQ(); // if this fails, method returns and is rerun by a driver class every ~5 seconds.
            }
        } else {
            System.out.println("No messages in list! please generate some messages.");
        }
    }

    public void connectRabbitMQ() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setPort(Integer.parseInt(rabbitmqPort));
        factory.setUsername(rabbitmqUser);
        factory.setPassword(rabbitmqPass);
        conn = null;
        chnl = null;
        while (conn == null || chnl == null) {
            try {
                conn = factory.newConnection();
                chnl = conn.createChannel();
            } catch (IOException | TimeoutException e) {
                System.out.println("Unable to create connection to rabbitmq, trying again...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    System.out.println("Thread interrupted");
                }
            }
        }
        try {
            chnl.queueDeclare(
                rabbitmqQueue,
                    false,
                    false,
                    false,
                    null);
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
    }
}
