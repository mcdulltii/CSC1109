package com.atm;
import java.util.Date;

public class Transaction {
    private int transactionCount = 0;
    private Account a1;
    private Date transactionDate;

    public Transaction(Account a1){
        //to add more fields
        this.a1=a1; 
        this.transactionDate=new Date();
    }

    public boolean hasAvailableBalance(double amount){
        if (amount < a1.getAvailableBalance()){
            return true;
        }
        return false;
    }

    public boolean belowTransferLimit(double amount){
        if (amount < a1.getTransferLimit()){
            return true;
        }
        return false;
    }
    
    //transfer between accounts
    public String transferToAccount(Account a1, Account a2, double amount){
        double newBalance=a1.getTotalBalance()-amount;
        a1.setTotalBalance(newBalance);
        newBalance=a2.getTotalBalance()+amount;
        a2.setTotalBalance(newBalance);
        return "Tranfer is Successful";
    }

    //deposit
    public String deposit(Account a1, double amount){
        double newBalance=a1.getTotalBalance()+amount;
        a1.setTotalBalance(newBalance);
        SQLQueries q = new SQLQueries();
        java.sql.Date sqlDate=new java.sql.Date(transactionDate.getTime());
        q.executeQueryTransactions(Integer.toString(this.transactionCount++), a1.getAccountNumber(), sqlDate, "", "", sqlDate, 0.0, amount, a1.getTotalBalance());
        return "Deposit Successful";
    }

    //withdraw
    public String withdraw(Account a1, double amount){
        if(a1.getAvailableBalance()>amount){
            double newBalance=a1.getTotalBalance()-amount;
            a1.setTotalBalance(newBalance);
            return "Withdraw Successful";
        }
        return "Withdraw Unsuccessful";
    }
}
