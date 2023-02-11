public class Account extends Settings {
    private String accountNumber;
    // private int pinNumber;
    private double availableBalance;
    private double totalBalance;
    private double transferLimit;
    private boolean isAuthenticated;
    private boolean isAdmin;
	private Authenticate auth;

	public Account(String username) {
		super(username);
		auth = new Authenticate();
		this.accountNumber = auth.generateAccountNumber();
        this.isAuthenticated = false;
        this.isAdmin = false;
	}

    public Account(String username, boolean isAdmin) {
		super(username);
		auth = new Authenticate();
		this.accountNumber = auth.generateAccountNumber();
        this.isAuthenticated = false;
        this.isAdmin = isAdmin;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
}