package com.atm.backend;

import java.sql.Connection;
import java.util.Date;

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

    protected boolean hasAvailableBalance(double amount) {
        if (amount < a1.getAvailableBalance()) {
            return true;
        }
        return false;
    }

    protected boolean belowTransferLimit(double amount) {
        if (amount < a1.getTransferLimit()) {
            return true;
        }
        return false;
    }
    
    protected abstract String execute(Account a1, double amount) throws InsufficientFundsException;
}

class InsufficientFundsException extends Exception {
    private double amount;

    protected InsufficientFundsException(double amount) {
        this.amount = amount;
    }

    protected double getAmount() {
        return amount;
    }
}
