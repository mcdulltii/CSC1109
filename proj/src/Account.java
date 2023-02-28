public class Account {
    private String accountNumber;
    private double availableBalance;
    private double totalBalance;
    protected double transferLimit;
    private boolean isAuthenticated;
	protected static Settings settings;

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
    }

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public double getTransferLimit() {
		return this.transferLimit;
	}

    public double getAvailableBalance(){
        return this.availableBalance;
    }

    public double getTotalBalance(){
        return this.totalBalance;
    }

    public void setTotalBalance(double balance){
        this.totalBalance=balance;
    }
}