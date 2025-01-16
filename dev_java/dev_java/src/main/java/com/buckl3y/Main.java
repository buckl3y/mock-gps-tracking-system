package com.buckl3y;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world");
        Double lat = 1000.0;
        Double lon = 1000.0;
        ZonedDateTime time = ZonedDateTime.now(ZoneId.systemDefault());
        Message message1 = new Message("CNDHWI1827", time, lat, lon);
        System.out.println(message1);
    }
}
