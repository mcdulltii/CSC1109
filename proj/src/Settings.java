public class Settings {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    
    public Settings(String username) {
        this.username = username;
    }

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
