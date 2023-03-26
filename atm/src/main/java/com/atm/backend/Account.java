package com.atm.backend;

import java.util.Random;

public class Account {
    final int accountNumberLength = 10;
    private String accountNumber;
    private double availableBalance;
    private double totalBalance;
    protected double transferLimit;

    public Account(double availableBalance,
            double totalBalance, double transferLimit) {
        super();
        this.accountNumber = this.getNewAccountNumber();
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
        this.transferLimit = transferLimit;
    }

    public Account(String accountNumber, double availableBalance,
            double totalBalance, double transferLimit) {
        super();
        this.accountNumber = accountNumber;
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
        this.transferLimit = transferLimit;
    }

    protected String getAccountNumber() {
        return this.accountNumber;
    }

    protected double getTransferLimit() {
        return this.transferLimit;
    }

    protected void setTransferLimit(double transferLimit) {
        this.transferLimit = transferLimit;
    }

    protected double getAvailableBalance() {
        return this.availableBalance;
    }

    protected double getTotalBalance() {
        return this.totalBalance;
    }

    protected void setTotalBalance(double balance) {
        this.totalBalance = balance;
    }

    protected void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    private String getNewAccountNumber() {
        Random rng = new Random();
        boolean nonUnique;
        String uuid;
        int len = this.accountNumberLength;

        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer) rng.nextInt(10)).toString();
            }
            nonUnique = false;
            // Check accounts for accountNumber collisions
            // for (Account a : this.accounts) {
            // if (uuid.compareTo(a.getAccountNumber()) == 0) {
            // nonUnique = true;
            // break;
            // }
            // }
        } while (nonUnique);
        return uuid;
    }

}
