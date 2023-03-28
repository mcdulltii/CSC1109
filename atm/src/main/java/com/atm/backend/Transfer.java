package com.atm.backend;

import java.util.*;

// transfer from a1 to a2
public class Transfer{
    private Withdraw withdrawal;
    private Deposit deposit;

    Transfer (Withdraw withdrawal, Deposit deposit){
        this.withdrawal = new Withdraw();
        this.deposit = new Deposit();
    }

    protected String transferToAccount(Account a1, Account a2, double amount) throws InsufficientFundsException {
        if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }
        
        withdrawal = new Withdraw(a1, a1.getAccountNumber(), "ATM WITHDRAWAL/TRF", UUID.randomUUID().toString(), new java.sql.Date(Calendar.getInstance().getTime().getTime()), amount, 0.0, a1.getTotalBalance());
        deposit = new Deposit(a2, a2.getAccountNumber(), "ATM DEPOSIT/TRF", UUID.randomUUID().toString(), new java.sql.Date(Calendar.getInstance().getTime().getTime()), 0.0, amount, a2.getTotalBalance());

        // Update transactions
        withdrawal.execute(a1, amount);
        deposit.execute(a2, amount);

        return "Tranfer is Successful";
    }

}