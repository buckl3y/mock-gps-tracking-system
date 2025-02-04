package com.buckl3y;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GPSWriter implements OffloadListener{

    private final String hostDB;
    private final int portDB;
    private final String nameDB;
    private final String userDB;
    private final String passDB;
    private final String table;
    private String urlDB;

    public GPSWriter(String host, int port, String name, String user, String pass, String table) {
        this.hostDB = host;
        this.portDB = port;
        this.nameDB = name;
        this.userDB = user;
        this.passDB = pass;
        this.table = table;
    }

    @Override
    public void batchOffload(List<Message> batch) {
        updateDB(new ArrayList<>(batch));
    }

    private void setURL() {
        urlDB = "jdbc:postgresql://" + hostDB + ":" + portDB + "/" + nameDB;
    }

    public void updateDB(ArrayList<Message> batch) {
        Connection conn = connectDB();
        String sqlInsertAndUpdate = "INSERT INTO " + table + " (serial_number, latitude, timestamp, longitude) " +
        "VALUES(?,?,?,?) " +
        "ON CONFLICT (serial_number) " +
        "DO UPDATE SET " +
        "timestamp = EXCLUDED.timestamp, " + 
        "latitude = EXCLUDED.latitude, " +
        "longitude = EXCLUDED.longitude";
        try {
            PreparedStatement statement = conn.prepareStatement(sqlInsertAndUpdate);
            for(Message message : batch) {
                statement.setString(1, message.getSerialNumber());
                statement.setDouble(2, message.getLatitude());
                statement.setObject(3, message.getTime().toOffsetDateTime());
                statement.setDouble(4, message.getLongitude());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            System.out.println("An SQL Exception has occured.");
        }
        disconnectDB(conn);
    }
    
    private Connection connectDB() {
        setURL();
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(urlDB, userDB, passDB);
        } catch (SQLException |ClassNotFoundException e) {
            System.out.println("A failure has occured when connecting to: " + hostDB);
        }
        return conn;
    }

    private boolean disconnectDB(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Unable to close connection to database.");
        }
        return conn == null;
    }
}
