package com.atm.backend;

import java.sql.Connection;
import java.util.*;

// transfer from a1 to a2
public class Transfer {
    private Withdraw withdrawal;
    private Deposit deposit;
    private Connection conn;

    public Transfer(Connection conn) {
        this.conn = conn;
    }

    // Updates accounts and transaction tables in database after transfer using Withdraw and Deposit objects
    //
    // # Arguments
    //
    // * `a1` - Sender Account  
    // * `a2` - Receiver Account
    // * `amount` - Transfer amount
    //
    // # Return String
    //
    // Successful message 
    protected String transferToAccount(Account a1, Account a2, double amount) throws InsufficientFundsException {
        if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }
        
        this.withdrawal = new Withdraw(a1, conn, a1.getAccountNumber(), "ATM WITHDRAWAL/TRF", UUID.randomUUID().toString(), new java.sql.Date(Calendar.getInstance().getTime().getTime()), amount, 0.0, a1.getTotalBalance());
        this.deposit = new Deposit(a2, conn, a2.getAccountNumber(), "ATM DEPOSIT/TRF", UUID.randomUUID().toString(), new java.sql.Date(Calendar.getInstance().getTime().getTime()), 0.0, amount, a2.getTotalBalance());

        // Update transactions
        this.withdrawal.execute(a1, amount);
        this.deposit.execute(a2, amount);

        return "Tranfer is Successful";
    }
}