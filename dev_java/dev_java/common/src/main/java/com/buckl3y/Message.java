package com.buckl3y;
import java.time.ZonedDateTime;

public class Message {
    
    String serialNumber;
    ZonedDateTime time;
    Double latitude;
    Double longitude;

    public Message() {
        
    }
    public Message(String sn, ZonedDateTime time, Double lat, Double lon) {
        this.serialNumber = sn;
        this.time = time;
        this.latitude = lat;
        this.longitude = lon;
    }

    @Override
    public String toString() {
        return 
        "{\nSerial Number: " + this.serialNumber + ","
        + "\nTimestamp: " + this.time + ","
        + "\nLatitude: " + this.latitude + ","
        + "\nLongitude: " + this.longitude + "\n}";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}