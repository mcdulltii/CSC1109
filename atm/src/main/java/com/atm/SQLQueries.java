package com.atm;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;

public class SQLQueries {
    final String db_url="jdbc:mysql://localhost:3306/oopasgdb";
    public String transactionId;

    public void executeQueryTransactions(String accountNumber, Date transactionDate, String transactionDetails, String chqNumber, Date valueDate, Double withdrawal, Double deposit, Double balance){
        Connection conn = getConnection();
        String getTransactionCount = "select transactionId from transactions order by transactionId desc limit 1";
        String sql = "insert into transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        ResultSet transactionCountResult = executeQuery(getTransactionCount);
        try {
            if (transactionCountResult != null)
                while (transactionCountResult.next())
                    transactionId = Integer.toString(transactionCountResult.getInt(1) + 1);
        } catch (Exception e) {
            System.out.println(e);
        }

        // Transaction table is empty
        if (transactionId == null)
            transactionId = "0";

        try {
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
        } catch (Exception e) {
            System.out.println(e);
        }

        closeConnection(conn);
    }

    public void importAccounts() throws FileNotFoundException{
        String sql = "SELECT * FROM accounts;";
        ResultSet sqlResult = executeQuery(sql);
        if (sqlResult == null) {
            // Import CSV

        }
    }

    private ResultSet executeQuery(String query) {
        ResultSet statementResult = null;
        Connection conn = getConnection();

        try {
            Statement sqlStatement = conn.createStatement();
            statementResult = sqlStatement.executeQuery(query);
        } catch (Exception e) {
            System.out.println(e);
        }

        closeConnection(conn);
        return statementResult;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(db_url, "testAdmin", "password1");
        } catch (Exception e){
            System.out.println(e);
        }
        return conn;
    }

    private void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            return;
        }
    }
}
