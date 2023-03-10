package com.atm;

import java.util.Random;

public class Account {
    final int accountNumberLength = 10;
    private String accountNumber;
    private double availableBalance;
    private double totalBalance;
    protected double transferLimit;
    private boolean isAuthenticated;
    protected static Settings settings;

    public Account(double availableBalance,
            double totalBalance, double transferLimit,
            boolean isAuthenticated) {
        super();
        this.accountNumber = this.getNewAccountNumber();
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
        this.transferLimit = transferLimit;
        this.isAuthenticated = isAuthenticated;
        settings = new Settings(this);
        storeAccountNumber();
    }

    public Account(String accountNumber, double availableBalance,
            double totalBalance, double transferLimit,
            boolean isAuthenticated) {
        super();
        this.accountNumber = accountNumber;
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
        this.transferLimit = transferLimit;
        this.isAuthenticated = isAuthenticated;
        settings = new Settings(this);
        storeAccountNumber();
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public double getTransferLimit() {
        return this.transferLimit;
    }

    public double getAvailableBalance() {
        return this.availableBalance;
    }

    public double getTotalBalance() {
        return this.totalBalance;
    }

    public void setTotalBalance(double balance) {
        this.totalBalance = balance;
    }

    public void setAvailableBalance(double availableBalance) {
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

    private void storeAccountNumber() {
        // TODO Store new account in accounts sql table
    }
}
