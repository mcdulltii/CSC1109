import java.util.Date;

public class Transaction {

    private Account a1;
    private double amount;
    private Date transactionDate;

    public Transaction(Account a1, double amount){
        //to add more fields
        this.a1=a1;
        this.amount=amount;   
        this.transactionDate=new Date();
    }

    public boolean hasAvailableBalance(){
        if (this.amount < a1.getAvailableBalance()){
            return true;
        }
        return false;
    }

    public boolean belowTransferLimit(){
        if (this.amount < a1.getTransferLimit()){
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
        //testing
        q.executeQueryTransactions("111111", "123", sqlDate, "", "", sqlDate, 0.0, amount, a1.getTotalBalance());
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
