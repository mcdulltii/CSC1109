package com.atm.backend;

import java.sql.Connection;
import java.util.UUID;

// subclass 
public class Withdraw extends Transaction {
    private Double withdrawal;
    private Double deposit = 0.0;

    public Withdraw(Account a1, Connection conn, String accountNumber, String transactionDetails,
            String chqNumber, java.sql.Date valueDate, Double withdrawal, Double deposit, Double balance) {
        super(a1, conn, accountNumber, transactionDetails, chqNumber, valueDate, balance);
        this.withdrawal = withdrawal;
    }

    public Withdraw(Account a1, Connection conn) {
        super(a1, conn);
    }

    public double getWithdrawal(){
        return withdrawal;
    }
// Updates accounts and transaction tables in database after withdrawal
    //
    // # Arguments
    //
    // * `a1` - Account 
    // * `amount` - Withdrawal amount
    //
    // # Return value
    //
    // Successful message 
    protected String execute(Account a1, double amount) throws InsufficientFundsException  {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        } else if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }

        // Update balances
        double newTotalBalance = a1.getTotalBalance() - amount;
        double newAvailableBalance = a1.getAvailableBalance() - amount;
        a1.setTotalBalance(newTotalBalance);
        a1.setAvailableBalance(newAvailableBalance);

        // Update transactions
        q.executeQueryTransactions(a1.getAccountNumber(), new java.sql.Date(super.getTransactionDate().getTime()), "ATM WITHDRAWAL/TRF", UUID.randomUUID().toString(), amount, deposit, newTotalBalance);

        // Update account balance
        q.executeQueryAccounts(a1, null);

        return "Withdraw Successful";
    }
}