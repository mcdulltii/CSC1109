package com.atm;

import java.util.Date;

public class Transaction {
    private Account a1;
    private Date transactionDate;

    public Transaction(Account a1) {
        // to add more fields
        this.a1 = a1;
        this.transactionDate = new Date();
    }

    // transfer between accounts
    public String transferToAccount(Account a1, Account a2, double amount) throws InsufficientFundsException {
        if (amount>a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance()-amount));
        }
        if (amount <0){
            throw new IllegalArgumentException("Amount has to be positive.");
        }
        SQLQueries q = new SQLQueries();
        q.executeQueryAccounts(a1, "transfer", amount, a2);

        return "Tranfer is Successful";
    }

    // deposit
    public String deposit(Account a1, double amount) {
        if (amount <0){
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update transactions
        SQLQueries q = new SQLQueries();
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        q.executeQueryTransactions(a1.getAccountNumber(), sqlDate, "", "", sqlDate, 0.0, amount, a1.getTotalBalance());

        // Update account balance -> deposit
        q.executeQueryAccounts(a1, "deposit", amount, null);

        return "Deposit Successful";
    }

    // withdraw
    public String withdraw(Account a1, double amount) throws InsufficientFundsException {
        if (amount>a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance()-amount));
        }
        if (amount <0){
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update account balance -> withdraw
        SQLQueries q = new SQLQueries();
        q.executeQueryAccounts(a1, "withdraw", amount, null);

        return "Withdraw Successful";
    }
}

class InsufficientFundsException extends Exception {
    private double amount;
    public InsufficientFundsException(double amount) { this.amount = amount; }
    public double getAmount() { return amount; }
}
