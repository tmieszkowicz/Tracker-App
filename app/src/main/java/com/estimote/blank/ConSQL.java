package com.estimote.blank;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConSQL {

    Connection connection;

    @SuppressLint("NewApi")
    public Connection conClass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String ip = "192.168.1.13:1433", database = "deviceLocations", username = "login", password = "password";
        ConnectionURL = "jdbc:jtds:sqlserver://" + ip + ";databaseName=" + database + ";user=" + username + ";password=" + password + ";";
        try {
            DriverManager.setLoginTimeout(1);
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
