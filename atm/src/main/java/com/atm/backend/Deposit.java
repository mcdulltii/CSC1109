package com.atm.backend;

import java.sql.Connection;
import java.util.UUID;

// subclass
public class Deposit extends Transaction {
    private Double withdrawal = 0.0;
    private Double deposit;

    public Deposit(Account a1, Connection conn, String accountNumber, String transactionDetails,
            String chqNumber, java.sql.Date valueDate, Double withdrawal, Double deposit, Double balance) {
        super(a1, conn, accountNumber, transactionDetails, chqNumber, valueDate, balance);
        this.deposit = deposit;
    }

    public Deposit(Account a1, Connection conn) {
        super(a1, conn);
    }

    public double getDeposit(){
        return deposit;
    }
    
// Updates accounts and transaction tables in database after deposit
    //
    // # Arguments
    //
    // * `a1` - Account 
    // * `amount` - Deposit amount
    //
    // # Return value
    //
    // Successful message 
    protected String execute(Account a1, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }

        // Update balance
        double newTotalBalance = a1.getTotalBalance() + amount;
        a1.setTotalBalance(newTotalBalance);

        //Update account balance
        q.executeQueryAccounts(a1, null);

        // Update transactions
        q.executeQueryTransactions(a1.getAccountNumber(),new java.sql.Date(super.getTransactionDate().getTime()), "ATM DEPOSIT/TRF", UUID.randomUUID().toString(), withdrawal, amount, newTotalBalance);
        return "Deposit Successful";
    }
}