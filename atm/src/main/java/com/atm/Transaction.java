package com.atm;

import java.util.Date;

public class Transaction {
    private Account a1;
    private String accountNumber;
    private Date transactionDate;
    private String transactionDetails;
    private String chqNumber;
    private java.sql.Date valueDate;
    private Double withdrawal;
    private Double deposit;
    private Double balance;

    public String getAccountNumber() {
        return accountNumber;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public String getChqNumber() {
        return chqNumber;
    }

    public java.sql.Date getValueDate() {
        return valueDate;
    }

    public Double getWithdrawal() {
        return withdrawal;
    }

    public Double getDeposit() {
        return deposit;
    }

    public Double getBalance() {
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

    public boolean hasAvailableBalance(double amount) {
        if (amount < a1.getAvailableBalance()) {
            return true;
        }
        return false;
    }

    public boolean belowTransferLimit(double amount) {
        if (amount < a1.getTransferLimit()) {
            return true;
        }
        return false;
    }

    // transfer between accounts
    public String transferToAccount(Account a1, Account a2, double amount) throws InsufficientFundsException {
        if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }
        SQLQueries q = new SQLQueries();
        q.executeQueryAccounts(a1, a2);
        // Update transactions
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "MoneyForYou", "554433", sqlDate, amount,
                0.0, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);
        transaction = new Transaction(a2, a2.getAccountNumber(), "MoneyForYou", "223344", sqlDate, amount,
                0.0, a2.getTotalBalance());
        q.executeQueryTransactions(transaction);

        return "Tranfer is Successful";
    }

    // deposit
    public String deposit(Account a1, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update transactions
        SQLQueries q = new SQLQueries();
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        // public Transaction(Account a1, String accountNumber, String
        // transactionDetails,
        // String chqNumber, Date valueDate, Double withdrawal, Double balance) {
        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "MoneyForYou", "123987", sqlDate, 0.0,
                amount, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);

        // a1.getAccountNumber(), sqlDate, "", "",sqlDate, 0.0, amount,
        // a1.getTotalBalance()

        // Update account balance -> deposit
        q.executeQueryAccounts(a1, null);

        return "Deposit Successful";
    }

    // withdraw
    public String withdraw(Account a1, double amount) throws InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        } else if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }

        double newTotalBalance = a1.getTotalBalance() - amount;
        double newAvailableBalance = a1.getAvailableBalance() - amount;
        a1.setTotalBalance(newTotalBalance);
        a1.setAvailableBalance(newAvailableBalance);

        SQLQueries q = new SQLQueries();
        // Update transactions
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "MoneyForYou", "996633", sqlDate,
                amount, 0.0, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);

        // Update account balance -> withdraw
        q.executeQueryAccounts(a1, null);

        return "Withdraw Successful";
    }
}

class InsufficientFundsException extends Exception {
    private double amount;

    public InsufficientFundsException(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
