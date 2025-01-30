package com.buckl3y;

public class Main {

    public static void main(String[] args) {
        final String hostDB = System.getenv("POSTGRES_HOST");
        final int portDB = Integer.parseInt(System.getenv("POSTGRES_PORT"));
        final String nameDB = System.getenv("POSTGRES_DB");
        final String userDB = System.getenv("POSTGRES_USER");
        final String passDB = System.getenv("POSTGRES_PASSWORD");
        final String rabbitmqQueue = System.getenv("RABBITMQ_QUEUE_NAME");
        final String rabbitmqUser = System.getenv("RABBITMQ_DEFAULT_USER");
        final String rabbitmqPass = System.getenv("RABBITMQ_DEFAULT_PASS");
        final String rabbitmqHost = System.getenv("RABBITMQ_HOST");
        final int rabbitmqPort = Integer.parseInt(System.getenv("RABBITMQ_PORT"));

        GPSWriter writer = new GPSWriter(hostDB, portDB, nameDB, userDB, passDB);
        Consumer consumer = new Consumer(rabbitmqHost, rabbitmqPort, rabbitmqQueue, rabbitmqUser, rabbitmqPass, writer);
        consumer.consumeMessages();
    }
}