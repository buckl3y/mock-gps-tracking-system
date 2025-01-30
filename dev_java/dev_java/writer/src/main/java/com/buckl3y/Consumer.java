package com.buckl3y;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {

    private final String host;
    private final int port;
    private final String queue;
    private final String user;
    private final String pass;
    private Connection conn = null;
    private Channel chnl = null;

    private final int BATCH_SIZE = 100;
    private final ArrayList<String> messages = new ArrayList<>();
    private final OffloadListener listener;
    
    public Consumer(String host, int port, String queue, String user, String pass, OffloadListener listener) {
        this.host = host;
        this.port = port;
        this.queue = queue;
        this.user = user;
        this.pass = pass;
        this.listener = listener;
    }
    
    //Consumes messages from defined queue. 
    public void consumeMessages() {
        RabbitMQManager rabbitMQManager = new RabbitMQManager(host, port, queue, user, pass);
        rabbitMQManager.connectRabbitMQ(); // conn and chnl must be assigned AFTER connectRabbitMQ(), else they will be null
        conn = rabbitMQManager.getConnection();
        chnl = rabbitMQManager.getChannel();

        //Callback function to continously consume messages
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            synchronized (messages) {
                String message = new String(delivery.getBody(), "UTF-8");
                messages.add(message);
                if(messages.size() >= BATCH_SIZE) {
                    offloadBatch(); 
                }
            }
        };
        try {
            chnl.basicConsume(queue, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            System.out.println("Failure when consuming message");
        }
    }

    public void offloadBatch() {
        /*clears messages array and delivers batch via callback to GPSWriter Class.
         * 
         * The GPSWriter class implements the OffloadListener interface, which defines the batchOffload() method. 
         * When the Consumer class calls batchOffload(List<String>), the overridden method in GPSWriter is triggered, 
         * which in turn invokes the updateDB() method to write the batch of messages to the database.
         * 
        */
        List<String> batch;
        synchronized (messages) {
            batch = new ArrayList<>(messages);
            messages.clear();
        }
        processBatch(batch);
    } 

    private void processBatch(List<String> batch) {
        System.out.println("Offloading #" + batch.size() + " messages.");
        if(listener != null) {
            listener.batchOffload(batch);
        }
    }
}
