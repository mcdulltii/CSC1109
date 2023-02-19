public class Settings {
	private User user;
	// private User account;

	Settings(User user) {
		this.user = user;
	}

	// Settings(Account account) {
	// 	this.account = account;
	// }
    
    public void setFirstName(String firstName) {
        user.firstName = firstName;
    }

	public void setLastName(String lastName) {
		user.lastName = lastName;
    }

    public void setPinNumber(String pinNumber) {
		user.pinNumber = pinNumber;
    }

	// public void setTransferLimit(double limit) {
		// account.transferLimit = limit;
	// }
}
