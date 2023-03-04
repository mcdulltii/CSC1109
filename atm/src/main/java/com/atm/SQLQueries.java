package com.atm;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class SQLQueries {
    final String db_url="jdbc:mysql://localhost:3306/oopasgdb";
    public String transactionId;

    public void executeQueryTransactions(String accountNumber, Date transactionDate, String transactionDetails, String chqNumber, Date valueDate, Double withdrawal, Double deposit, Double balance){
        Connection conn = getConnection();
        String getTransactionCount = "SELECT transactionId FROM transactions ORDER BY transactionId desc limit 1";
        String sql = "INSERT INTO transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
    }

    public void importAccounts() throws FileNotFoundException{
        String sql = "SELECT * FROM accounts";
        ResultSet rs = executeQuery(sql);
        try {
            if (!rs.next()) {
                // Import CSV
                BufferedReader br = new BufferedReader(new FileReader("atm/res/accounts.csv"));
                Authenticate au = new Authenticate();
                try {
                    br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Connection conn = getConnection();
                while (true) {
                    String row = null;
                    try {
                        row = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (row == null) break;
                    sql = "INSERT INTO accounts(AccountNumber, UserName, Password, FirstName, LastName, PinNumber, AvailableBalance, TotalBalance, TransferLimit, IsAdmin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStmt = conn.prepareStatement(sql);
                    String[] data = row.split(",");
                    for (int i=0; i<data.length; i++) {
                        switch(i) {
                            case 0:
                                preparedStmt.setLong(i+1, Long.parseLong(data[i]));
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                preparedStmt.setString(i+1, data[i]);
                                break;
                            case 5:
                                preparedStmt.setLong(i+1, Long.parseLong(data[i]));
                                preparedStmt.setString(3, au.hashString(data[i]));
                                break;
                            case 6:
                            case 7:
                            case 8:
                                preparedStmt.setFloat(i+1, Float.parseFloat(data[i]));
                                break;
                            case 9:
                                preparedStmt.setBoolean(i+1, Boolean.parseBoolean(data[i]));
                        }
                    }
                    preparedStmt.execute();
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        //closeConnection(conn);
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
