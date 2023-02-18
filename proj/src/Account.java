public class Account {
    private String accountNumber;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private int pinNumber;
    private double availableBalance;
    private double totalBalance;
    private double transferLimit;
    private boolean isAuthenticated;
    private boolean isAdmin;

	public Account(String accountNumber, String username, String password, String firstName, String lastName, int pinNumber,
            double availableBalance, double totalBalance, double transferLimit, boolean isAuthenticated, boolean isAdmin) {
        super();
        this.accountNumber = accountNumber;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinNumber = pinNumber;
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
        this.transferLimit = transferLimit;
        this.isAuthenticated = isAuthenticated;
        this.isAdmin = isAdmin;
    }

	public String getAccountNumber() {
		return accountNumber;
	}

	//missing getters, oh well :/
}