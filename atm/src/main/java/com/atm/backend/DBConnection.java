package com.atm.backend;

import java.sql.*;

public class DBConnection {
    public Connection getConnection() {
        Connection conn = null;
        final String db_url = "jdbc:mysql://localhost:3306/oopasgdb";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(db_url, "testAdmin", "password1");
        } catch (Exception e) {
            System.out.println(e);
        }

        return conn;
    }
}
