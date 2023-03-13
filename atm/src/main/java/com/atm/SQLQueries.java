package com.atm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class SQLQueries {
    final String db_url = "jdbc:mysql://localhost:3306/oopasgdb";
    public String transactionId;

    public void executeQueryTransactions(Transaction tr) {
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
            preparedStmt.setString(2, tr.getAccountNumber());
            preparedStmt.setDate(3, tr.getValueDate());
            preparedStmt.setString(4, tr.getTransactionDetails());
            preparedStmt.setString(5, tr.getChqNumber());
            preparedStmt.setDate(6, tr.getValueDate());
            preparedStmt.setDouble(7, tr.getWithdrawal());
            preparedStmt.setDouble(8, tr.getDeposit());
            preparedStmt.setDouble(9, tr.getBalance());
            preparedStmt.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Update accounts table based on selected action (Deposit/Withdraw)
    public void executeQueryAccounts(Account a1, Account a2) {
        Connection conn = getConnection();

        String updateQuery = "UPDATE accounts SET TotalBalance = ?, AvailableBalance = ?, TransferLimit = ? WHERE AccountNumber = ?";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
            preparedStmt.setDouble(1, a1.getTotalBalance());
            preparedStmt.setDouble(2, a1.getAvailableBalance());
            preparedStmt.setDouble(3, a1.getTransferLimit());
            preparedStmt.setLong(4, Long.parseLong(a1.getAccountNumber()));
            preparedStmt.executeUpdate();
            if (a2 != null) {
                preparedStmt.setDouble(1, a2.getTotalBalance());
                preparedStmt.setDouble(2, a2.getAvailableBalance());
                preparedStmt.setDouble(3, a2.getTransferLimit());
                preparedStmt.setLong(4, Long.parseLong(a2.getAccountNumber()));
                preparedStmt.executeUpdate();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Create and return Account object from accounts table based on username input
    public Account getAccountfromUsername(String username) {
        double availableBalance = 0, totalBalance = 0, transferLimit = 0;
        long accountNumber = 0;

        String selectQuery = "SELECT * FROM accounts WHERE UserName = \"" + username + "\"";
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                accountNumber = rs.getLong("AccountNumber");
                availableBalance = rs.getDouble("AvailableBalance");
                totalBalance = rs.getDouble("TotalBalance");
                transferLimit = rs.getDouble("TransferLimit");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        Account newAccount = new Account(String.valueOf(accountNumber), availableBalance,
                totalBalance, transferLimit);
        return newAccount;
    }

    public Account getAccountfromAccountNumber(Long accountNumber) {
        double availableBalance = 0, totalBalance = 0, transferLimit = 0;

        String selectQuery = "SELECT * FROM accounts WHERE AccountNumber = " + accountNumber;
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                availableBalance = rs.getDouble("AvailableBalance");
                totalBalance = rs.getDouble("TotalBalance");
                transferLimit = rs.getDouble("TransferLimit");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        Account newAccount = new Account(String.valueOf(accountNumber), availableBalance,
                totalBalance, transferLimit);
        return newAccount;
    }

    public String getPasswordfromUsername(String username) {
        String password = "";

        String selectQuery = "SELECT * FROM accounts WHERE UserName = \"" + username + "\"";
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                password = rs.getString("Password");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return password;
    }

    public void importAccounts() throws FileNotFoundException {
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
                    if (row == null)
                        break;

                    sql = "INSERT INTO accounts(CardNumber, AccountNumber, UserName, Password, FirstName, LastName, PinNumber, AvailableBalance, TotalBalance, TransferLimit, IsAdmin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStmt = conn.prepareStatement(sql);
                    String[] data = row.split(",");

                    for (int i = 0; i < data.length; i++) {
                        switch (i) {
                            case 0:
                            case 1:
                                preparedStmt.setLong(i + 1, Long.parseLong(data[i]));
                                preparedStmt.setLong(i + 1, Long.parseLong(data[i]));
                                break;
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                                preparedStmt.setString(i + 1, data[i]);
                                break;
                            case 6:
                                preparedStmt.setLong(i + 1, Long.parseLong(data[i]));
                                preparedStmt.setString(4, au.hashString(data[i]));
                                break;
                            case 7:
                            case 8:
                            case 9:
                                preparedStmt.setFloat(i + 1, Float.parseFloat(data[i]));
                                break;
                            case 10:
                                preparedStmt.setBoolean(i + 1, Boolean.parseBoolean(data[i]));
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
        // closeConnection(conn);
        return statementResult;
    }

    private Connection getConnection() {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(db_url, "testAdmin", "password1");
        } catch (Exception e) {
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
