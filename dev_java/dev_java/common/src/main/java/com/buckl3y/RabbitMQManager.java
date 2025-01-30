package com.buckl3y;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitMQManager {

    private final String rabbitmqQueue;
    private final String rabbitmqUser;
    private final String rabbitmqPass;
    private final String rabbitmqHost;
    private final int rabbitmqPort;
    private Connection conn;
    private Channel chnl;

    public Connection getConnection() {
        return conn;
    }

    public Channel getChannel() {
        return chnl;
    }

    public RabbitMQManager(String host, int port, String queue, String user, String pass) {
        this.rabbitmqQueue = queue;
        this.rabbitmqUser = user;
        this.rabbitmqPass = pass;
        this.rabbitmqHost = host;
        this.rabbitmqPort = port;
    }
    
    public void connectRabbitMQ() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setPort(rabbitmqPort);
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
            declareQueue(chnl, rabbitmqQueue);
        }
    }

    private void declareQueue(Channel chnl, String queue) {
        if(chnl != null) {
            try {
                chnl.queueDeclare(
                    queue,
                    false,
                    false,
                    false,
                    null);
            } catch (IOException e) {
                System.out.println("IO Exception");
            }
        }
    }   
}
