package com.atm.backend;

import java.sql.Connection;
import java.util.Date;

/// Inheritance - Superclass
/// Subclass - Withdraw.java and Deposit.java
public abstract class Transaction {
    private Account a1;
    private String accountNumber;
    private Date transactionDate;
    private String chqNumber;
    private String transactionDetails;
    private java.sql.Date valueDate;
    private Double balance;
    private Connection conn;
    protected SQLQueries q;

    public Transaction(Account a1, Connection conn) {
        this.a1 = a1;
        this.transactionDate = new Date();
        this.conn = conn;
        this.q = new SQLQueries(this.conn);
    }

    public Transaction(Account a1, Connection conn, String accountNumber, String transactionDetails,
            String chqNumber, java.sql.Date valueDate, Double balance) {
        this.a1 = a1;
        this.accountNumber = accountNumber;
        this.transactionDate = new Date();
        this.transactionDetails = transactionDetails;
        this.chqNumber = chqNumber;
        this.valueDate = valueDate;
        this.balance = balance;
        this.conn = conn;
        this.q = new SQLQueries(this.conn);
    }

    protected String getAccountNumber() {
        return accountNumber;
    }

    protected Date getTransactionDate() {
        return transactionDate;
    }

    protected String getTransactionDetails() {
        return transactionDetails;
    }

    protected String getChqNumber() {
        return chqNumber;
    }

    protected java.sql.Date getValueDate() {
        return valueDate;
    }

    protected Double getBalance() {
        return balance;
    }

    /// Abstract Method to be implemented in Withdraw and Deposit classes to execute action
    ///
    /// # Arguments
    ///
    /// \param a1 Account
    /// \param amount Withdrawal or Deposit amount 
    protected abstract String execute(Account a1, double amount) throws InsufficientFundsException;
}

/// User-defined Exception class to check that user have sufficient funds in account for withdrawal / transfer
class InsufficientFundsException extends Exception {
    private double amount;

    protected InsufficientFundsException(double amount) {
        this.amount = amount;
    }

    protected double getAmount() {
        return amount;
    }
}
