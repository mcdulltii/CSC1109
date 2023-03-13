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

    public Transaction(Account a1){
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
    public String transferToAccount(Account a1, Account a2, double amount) {
        double newTotalBalance = a1.getTotalBalance() - amount;
        double newTransferLimit = a1.getTransferLimit() - amount;
        a1.setTotalBalance(newTotalBalance);
        a1.setTransferLimit(newTransferLimit);

        newTotalBalance = a2.getTotalBalance() + amount;
        a2.setTotalBalance(newTotalBalance);

        SQLQueries q = new SQLQueries();
        q.executeQueryAccounts(a1, a2);

        return "Tranfer is Successful";
    }

    // deposit
    public String deposit(Account a1, double amount) {
        double newTotalBalance = a1.getTotalBalance() + amount;
        a1.setTotalBalance(newTotalBalance);

        // Update transactions
        SQLQueries q = new SQLQueries();
        java.sql.Date sqlDate = new java.sql.Date(transactionDate.getTime());
        //public Transaction(Account a1, String accountNumber, String transactionDetails, 
        //String chqNumber, Date valueDate, Double withdrawal, Double balance) {
        Transaction transaction = new Transaction(a1, a1.getAccountNumber(), "MoneyForYou", "123987", sqlDate, amount, 0.0, a1.getTotalBalance());
        q.executeQueryTransactions(transaction);

        //a1.getAccountNumber(), sqlDate, "", "",sqlDate, 0.0, amount, a1.getTotalBalance()
        
        // Update account balance -> deposit
        q.executeQueryAccounts(a1, null);

        return "Deposit Successful";
    }

    // withdraw
    public String withdraw(Account a1, double amount) {
        if (a1.getAvailableBalance() > amount) {
            double newTotalBalance = a1.getTotalBalance() - amount;
            double newAvailableBalance = a1.getAvailableBalance() - amount;
            a1.setTotalBalance(newTotalBalance);
            a1.setAvailableBalance(newAvailableBalance);

            // Update account balance -> withdraw
            SQLQueries q = new SQLQueries();
            q.executeQueryAccounts(a1, null);

            return "Withdraw Successful";
        }
        return "Withdraw Unsuccessful";
    }
}
