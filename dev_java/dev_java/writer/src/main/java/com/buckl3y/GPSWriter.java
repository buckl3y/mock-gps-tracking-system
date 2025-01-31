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
    private String urlDB;

    public GPSWriter(String host, int port, String name, String user, String pass) {
        this.hostDB = host;
        this.portDB = port;
        this.nameDB = name;
        this.userDB = user;
        this.passDB = pass;
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
        String sqlInsertAndUpdate = "INSERT INTO" + nameDB + "(serial_number, latitude, timestamp, longitude) VALUES(?,?,?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sqlInsertAndUpdate);
            for(Message message : batch) {
                statement.setString(1, message.getSerialNumber());
                statement.setString(2, message.getLatitude().toString());
                statement.setString(3, message.getTime().toString());
                statement.setString(4, message.getLongitude().toString());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            System.out.println("An SQL Exception has occured.");
        }
        System.out.println("Writing batch to Database...");
        disconnectDB(conn);
    }
    
    private Connection connectDB() {
        setURL();
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(urlDB, userDB, passDB);
            if(conn != null) {
                System.out.println("Connected to DB successfully. Hurray!");
            }
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
