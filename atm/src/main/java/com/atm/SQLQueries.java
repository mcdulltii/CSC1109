package com.atm;
import java.sql.*;
public class SQLQueries {
    public String transactionId;

    public void executeQueryTransactions(String accountNumber, Date transactionDate, String transactionDetails, String chqNumber, Date valueDate, Double withdrawal, Double deposit, Double balance){
        final String db_url="jdbc:mysql://localhost:3306/oopasgdb";
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(db_url, "testAdmin", "password1");
            String getTransactionCount = "select transactionId from transactions order by transactionId desc limit 1";
            Statement getTransactionCountStmt = conn.createStatement();
            ResultSet transactionCountResult = getTransactionCountStmt.executeQuery(getTransactionCount);
            while (transactionCountResult.next())
                transactionId = Integer.toString(transactionCountResult.getInt(1) + 1);
        } catch (Exception e){
            System.out.println(e);
        }

        // Transaction table is empty
        if (transactionId == null)
            transactionId = "0";

        if (conn != null) {
            try {
                String sql = "insert into transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStmt = conn.prepareStatement(sql);
                preparedStmt.setString(1, transactionId);
                preparedStmt.setString(2, accountNumber);
                preparedStmt.setDate(3, transactionDate);
                preparedStmt.setString(4, transactionDetails);
                preparedStmt.setString(5, chqNumber);
                preparedStmt.setDate(6, valueDate);
                preparedStmt.setDouble(7, withdrawal);
                preparedStmt.setDouble(8, deposit);
                preparedStmt.setDouble(9, balance);
                preparedStmt.execute();
                conn.close();
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
