package com.atm.backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.*;
import java.sql.Date;
import java.util.Scanner;

import me.tongfei.progressbar.ProgressBar;

public class SQLQueries {
    final String db_url = "jdbc:mysql://localhost:3306/oopasgdb";
    private String transactionId;

    protected void executeQueryTransactions(Transaction tr) {
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
    protected void executeQueryAccounts(Account a1, Account a2) {
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
            System.out.println("Unable to access database.");
        }
    }

    protected void executeQuerySettings(Account ac, String field) {
        Connection conn = getConnection();
        String updateQuery = "";
        PreparedStatement preparedStmt;
        long accNo = Long.parseLong(ac.getAccountNumber());

        try {
            switch (field) {
                case "transferlimit":
                    updateQuery = "UPDATE accounts SET TransferLimit = ? WHERE AccountNumber = ?";
                    preparedStmt = conn.prepareStatement(updateQuery);
                    preparedStmt.setDouble(1, ac.getTransferLimit());
                    preparedStmt.setLong(2, accNo);
                    preparedStmt.executeUpdate();
                    break;
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database.");
        }
    }

    protected void executeQuerySettings(User user, String field) {
        Connection conn = getConnection();
        String updateQuery = "";
        PreparedStatement preparedStmt;
        long accNo = Long.parseLong(user.getAccNo());

        try {
            switch (field) {
                case "pin":
                    updateQuery = "UPDATE accounts SET Password = ? WHERE AccountNumber = ?";
                    preparedStmt = conn.prepareStatement(updateQuery);
                    preparedStmt.setString(1, user.getPin());
                    preparedStmt.setLong(2, accNo);
                    preparedStmt.executeUpdate();
                    break;
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database.");
        }
    }

    // Create and return Account object from accounts table based on username input
    public AccUserObj getAccountfromUsername(String username) {
        String accountNumber = "", firstname = "", lastname = "";
        int isAdmin = 0;
        double availableBalance = 0, totalBalance = 0, transferLimit = 0;

        String selectQuery = "SELECT * FROM accounts WHERE UserName = \"" + username + "\"";
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                accountNumber = String.valueOf(rs.getLong("AccountNumber"));
                firstname = rs.getString("FirstName");
                lastname = rs.getString("LastName");
                availableBalance = rs.getDouble("AvailableBalance");
                totalBalance = rs.getDouble("TotalBalance");
                transferLimit = rs.getDouble("TransferLimit");
                isAdmin = rs.getInt("IsAdmin");
            }
        } catch (SQLException e) {
            System.out.println("Please check column label and database connection.");
        }

        Account newUserAcc = new Account(accountNumber, availableBalance,
                totalBalance, transferLimit);
        User newUser = new User(accountNumber, firstname, lastname, isAdmin);
        AccUserObj obj = new AccUserObj(newUserAcc, newUser);

        return obj;
    }

    protected Account getAccountfromAccountNumber(Long accountNumber) {
        double availableBalance = 0, totalBalance = 0, transferLimit = 0;

        String selectQuery = "SELECT * FROM accounts WHERE AccountNumber = " + accountNumber;
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                availableBalance = rs.getDouble("AvailableBalance");
                totalBalance = rs.getDouble("TotalBalance");
                transferLimit = rs.getDouble("TransferLimit");
            }
        } catch (SQLException e) {
            System.out.println("Please check column label and database connection.");
        }

        Account newAccount = new Account(String.valueOf(accountNumber), availableBalance,
                totalBalance, transferLimit);
        return newAccount;
    }

    protected String getPasswordfromUsername(String username) {
        String password = "";

        String selectQuery = "SELECT * FROM accounts WHERE UserName = \"" + username + "\"";
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                password = rs.getString("Password");
            }
        } catch (SQLException e) {
            System.out.println("Please check column label and database connection.");
        }

        return password;
    }

    private String getAdminPassword() {
        String password = "";

        String selectQuery = "SELECT * FROM accounts WHERE IsAdmin=1";
        ResultSet rs = executeQuery(selectQuery);

        try {
            while (rs.next()) {
                password = rs.getString("Password");
            }
        } catch (SQLException e) {
            System.out.println("Please check column label and database connection.");
        }

        return password;
    }

    public String importAdminAccount() {
        String sql = "SELECT * FROM accounts WHERE IsAdmin=1";
        ResultSet rs = executeQuery(sql);
        String passwordString = "";

        try {
            if (!rs.next()) {
                Scanner sc = new Scanner(System.in);
                Connection conn = getConnection();
                sql = "INSERT INTO accounts(CardNumber, AccountNumber, UserName, Password, FirstName, LastName, PinNumber, AvailableBalance, TotalBalance, TransferLimit, IsAdmin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStmt = conn.prepareStatement(sql);

                System.out.println("Admin account not found!");
                Boolean firstRun = true;
                while (passwordString.length() != 6 || !passwordString.matches("[0-9]{6}")) {
                    if (!firstRun)
                        System.out.println("Enter a 6 digit password!");
                    System.out.print("Enter admin password to set admin account: ");
                    passwordString = sc.nextLine().strip();
                    firstRun = false;
                }
                Authenticate au = new Authenticate();
                preparedStmt.setLong(1, 0); // Set CardNumber
                preparedStmt.setLong(2, 0); // Set AccountNumber
                preparedStmt.setString(3, "ADMIN"); // Set Username
                preparedStmt.setString(4, au.hashString(passwordString)); // Set Password
                preparedStmt.setString(5, "ADMIN"); // Set FirstName
                preparedStmt.setString(6, "ADMIN"); // Set LastName
                preparedStmt.setLong(7, Long.parseLong(passwordString)); // Set PinNumber
                preparedStmt.setFloat(8, 0); // Set AvailableBalance
                preparedStmt.setFloat(9, 0); // Set TotalBalance
                preparedStmt.setFloat(10, 0); // Set TransferLimit
                preparedStmt.setBoolean(11, true); // Set IsAdmin
                preparedStmt.execute();
            } else {
                passwordString = this.getAdminPassword();
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database.");
        }
        return passwordString;
    }

    public void importAccounts() throws FileNotFoundException {
        String sql = "SELECT * FROM accounts";
        ResultSet rs = executeQuery(sql);

        try {
            if (!rs.next()) {
                // Import CSV
                String filename = "atm/res/accounts.csv";
                BufferedReader br = new BufferedReader(new FileReader(filename));
                Authenticate au = new Authenticate();

                ProgressBar pb = new ProgressBar("Importing Accounts", countLines(filename));
                pb.start();

                try {
                    br.readLine();
                } catch (IOException e) {
                    System.out.println("Unable to read file.");
                }

                Connection conn = getConnection();
                while (true) {
                    pb.step();
                    
                    String row = null;
                    try {
                        row = br.readLine();
                    } catch (IOException e) {
                        System.out.println("Unable to read file.");
                        break;
                    }
                    if (row == null)
                        break;

                    sql = "INSERT INTO accounts(CardNumber, AccountNumber, UserName, Password, FirstName, LastName, PinNumber, AvailableBalance, TotalBalance, TransferLimit, IsAdmin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStmt = conn.prepareStatement(sql);
                    String[] data = row.split(",");

                    for (int i = 0; i < data.length; i++) {
                        switch (i) {
                            case 0: // Set CardNumber
                            case 1: // Set AccountNumber
                                preparedStmt.setLong(i + 1, Long.parseLong(data[i]));
                                break;
                            case 2: // Set Username
                            case 4: // Set FirstName
                            case 5: // Set LastName
                                preparedStmt.setString(i + 1, data[i]);
                                break;
                            case 6: // Set PinNumber and Password
                                preparedStmt.setLong(i + 1, Long.parseLong(data[i]));
                                preparedStmt.setString(4, au.hashString(data[i]));
                                break;
                            case 7: // Set AvailableBalance
                            case 8: // Set TotalBalance
                            case 9: // Set TransferLimit
                                preparedStmt.setFloat(i + 1, Float.parseFloat(data[i]));
                                break;
                            case 10: // Set IsAdmin
                                preparedStmt.setBoolean(i + 1, Boolean.parseBoolean(data[i]));
                        }
                    }
                    preparedStmt.execute();
                }

                pb.stop();

                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Unable to close database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database.");
        }
    }

    public void importTransactions(Boolean isPartial) throws FileNotFoundException {
        String sql = "SELECT * FROM transactions";
        ResultSet rs = executeQuery(sql);

        try {
            if (!rs.next()) {
                // Import CSV
                String filename = "atm/res/transactions_new.csv";
                BufferedReader br = new BufferedReader(new FileReader(filename));
                
                int lineNumbers = countLines(filename);
                ProgressBar pb = new ProgressBar("Importing Transactions", lineNumbers);
                pb.start();

                try {
                    br.readLine();
                } catch (IOException e) {
                    System.out.println("Unable to read file.");
                }

                if (isPartial)
                    System.out.println("Importing transactions database partially.");
                else {
                    System.out.println("Importing entire transactions database will take a long time.");
                    System.out.println("Provide `--partial` argument when running server to only import partially.");
                }

                Connection conn = getConnection();
                int count = 0;
                while (true) {
                    pb.step();
                    
                    String row = null;
                    try {
                        row = br.readLine();
                    } catch (IOException e) {
                        System.out.println("Unable to read file.");
                        break;
                    }
                    if (row == null || isPartial && count > (lineNumbers / 10))
                        break;

                    sql = "INSERT INTO transactions(transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStmt = conn.prepareStatement(sql);
                    String[] data = row.split(",");

                    for (int i = 0; i < data.length; i++) {
                        switch (i) {
                            case 0: // Set transactionId
                                preparedStmt.setString(i + 1, data[i]);
                                break;
                            case 1: // Set AccountNumber
                                preparedStmt.setLong(i + 1, Long.parseLong(data[i]));
                                break;
                            case 2: // Set transactionDate
                                preparedStmt.setDate(i + 1, Date.valueOf(data[i])); //date in string format yyyy-mm-dd
                                break;
                            case 3: // Set transactionDetails
                            case 4: // Set chqNumber
                                preparedStmt.setString(i + 1, data[i]);
                                break;
                            case 5: // Set valueDate
                                preparedStmt.setDate(i + 1, Date.valueOf(data[i])); //date in string format yyyy-mm-dd
                                break;
                            case 6: // Set withdrawal
                            case 7: // Set deposit
                            case 8: // Set balance
                                preparedStmt.setFloat(i + 1, Float.parseFloat(data[i]));
                                break;
                        }
                    }
                    preparedStmt.execute();
                    count++;
                }
                
                pb.stop();

                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Unable to close database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database.");
        }
    }

    private int countLines(String filename) {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(filename));
            reader.skip(Long.MAX_VALUE);
            return reader.getLineNumber();
        } catch (Exception ex) {
            System.out.println("Unable to get file line numbers.");
            return -1;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Unable to close FileReader.");
            }
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
