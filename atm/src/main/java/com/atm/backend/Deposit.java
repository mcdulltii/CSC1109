package com.atm.backend;

import java.sql.Connection;
import java.util.UUID;

public class Deposit extends Transaction{
    private Double withdrawal = 0.0;
    private Double deposit;

    Deposit(Account a1, String accountNumber, String transactionDetails,
            String chqNumber, java.sql.Date valueDate, Double withdrawal, Double deposit, Double balance) {
        super(a1, accountNumber,transactionDetails,chqNumber,valueDate,balance);
        this.deposit = deposit;
    }

    public Deposit(Account a1, Connection conn) {
        // to add more fields
        super(a1, conn);
    }

    public Deposit(){};

    public double getDeposit(){
        return deposit;
    }

    protected String execute(Account a1, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update balance
        double newTotalBalance = a1.getTotalBalance() + amount;
        a1.setTotalBalance(newTotalBalance);

        //Update account balance
        q.executeQueryAccounts(a1, null);
        
        // public Transaction(Account a1, String accountNumber, String
        // transactionDetails,
        // String chqNumber, Date valueDate, Double withdrawal, Double balance) {

        // Update transactions
        q.executeQueryTransactions(a1.getAccountNumber(),new java.sql.Date(super.getTransactionDate().getTime()), "ATM DEPOSIT/TRF", UUID.randomUUID().toString(), withdrawal, amount, newTotalBalance);
        return "Deposit Successful";
    }

}