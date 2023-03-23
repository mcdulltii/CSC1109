package com.atm.backend;

import java.util.Date;
import java.util.UUID;

public class Transaction {
    private Account a1;
    private String accountNumber;
    private Date transactionDate;
    private String chqNumber;
    private String transactionDetails;
    private java.sql.Date valueDate;
    private Double withdrawal;
    private Double deposit;
    private Double balance;

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

    protected Double getWithdrawal() {
        return withdrawal;
    }

    protected Double getDeposit() {
        return deposit;
    }

    protected Double getBalance() {
        return balance;
    }

    public Transaction(Account a1) {
        // to add more fields
        this.a1 = a1;
        this.transactionDate = new Date();
    }

    public Transaction(Account a1, String accountNumber, String transactionDetails,
            String chqNumber, java.sql.Date valueDate, Double withdrawal, Double deposit, Double balance) {
        // to add more fields
        this.a1 = a1;
        this.accountNumber = accountNumber;
        this.transactionDate = new Date();
        this.transactionDetails = transactionDetails;
        this.chqNumber = chqNumber;
        this.valueDate = valueDate;
        this.withdrawal = withdrawal;
        this.deposit = deposit;
        this.balance = balance;
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

    // transfer between accounts
    protected String transferToAccount(Account a1, Account a2, double amount) throws InsufficientFundsException {
        if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update balances
        double newTotalBalance = a1.getTotalBalance() - amount;
        double newTransferLimit = a1.getTransferLimit() - amount;
        a1.setTotalBalance(newTotalBalance);
        a1.setTransferLimit(newTransferLimit);

        SQLQueries q = new SQLQueries();
        q.executeQueryAccounts(a1, a2);
        
        newTotalBalance = a2.getTotalBalance() + amount;
        a2.setTotalBalance(newTotalBalance);
        
        // Update transactions
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "TRF TO "+a2.getAccountNumber(), UUID.randomUUID().toString(), sqlDate, amount,
                0.0, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);
        transaction = new Transaction(a2, a2.getAccountNumber(), "TRF FROM "+a1.getAccountNumber(), UUID.randomUUID().toString(), sqlDate, 0.0,
                amount, a2.getTotalBalance());
        q.executeQueryTransactions(transaction);

        return "Tranfer is Successful";
    }

    // deposit
    protected String deposit(Account a1, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update transactions
        SQLQueries q = new SQLQueries();
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());

        // Update balance
        double newTotalBalance = a1.getTotalBalance() + amount;
        a1.setTotalBalance(newTotalBalance);
        q.executeQueryAccounts(a1, null);

        a1.setTotalBalance(newTotalBalance);
        
        // public Transaction(Account a1, String accountNumber, String
        // transactionDetails,
        // String chqNumber, Date valueDate, Double withdrawal, Double balance) {

        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "ATM DEPOSIT", UUID.randomUUID().toString(), sqlDate, 0.0,
                amount, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);
        return "Deposit Successful";
    }

    // withdraw
    protected String withdraw(Account a1, double amount) throws InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        } else if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }

        // Update balances
        SQLQueries q = new SQLQueries();
        double newTotalBalance = a1.getTotalBalance() - amount;
        double newAvailableBalance = a1.getAvailableBalance() - amount;
        a1.setTotalBalance(newTotalBalance);
        a1.setAvailableBalance(newAvailableBalance);

        // Update transactions
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "ATM WITHDRAWAL", UUID.randomUUID().toString(), sqlDate,
                amount, 0.0, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);

        // Update account balance
        q.executeQueryAccounts(a1, null);

        return "Withdraw Successful";
    }
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
