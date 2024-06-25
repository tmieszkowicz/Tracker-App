package com.estimote.blank;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConSQL {

    private static final String DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    private static final String IP = "192.168.1.13:1433";
    private static final String DATABASE = "deviceLocations";
    private static final String USERNAME = "login";
    private static final String PASSWORD = "password";
    private static final String CONNECTION_URL = "jdbc:jtds:sqlserver://" + IP + ";databaseName=" + DATABASE + ";user=" + USERNAME + ";password=" + PASSWORD + ";";

    @SuppressLint("NewApi")
    public Connection conClass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;

        try {
            Class.forName(DRIVER);
            DriverManager.setLoginTimeout(1);
            connection = DriverManager.getConnection(CONNECTION_URL);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}