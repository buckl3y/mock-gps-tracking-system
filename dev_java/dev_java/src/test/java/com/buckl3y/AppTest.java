package com.buckl3y;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AppTest {
    @Test
    public void checkMessageConstructor() {
        String sn = "test_serial";
        ZonedDateTime time = ZonedDateTime.now();
        Double lat = 1234.56789;
        Double lon = 9876.54321;

        Message msg = new Message(sn, time, lat, lon);

        assertEquals(sn, msg.getSerialNumber());
        assertEquals(time, msg.getTime());
        assertEquals(lat, msg.getLatitude());
        assertEquals(lon, msg.getLongitude());
    }
}
