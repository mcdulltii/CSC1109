public class Transaction {

    private Account a1;
    private Account a2;
    private double amount;

    // transaction from a1 to a2
    public Transaction(Account a1, Account a2, double amount){
        this.a1=a1;
        this.a2=a2;
        this.amount=amount;
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
}
