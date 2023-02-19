public class User {
    // potentially temp method for userId
    static int userCounter = 0;

    private int userId;
    protected String firstName;
    protected String lastName;
    protected String pinNumber;
    private Settings settings;

    User(String firstName, String lastName, String pinNumber) {
        this.userId = userCounter;
        this.firstName = firstName;
        this.lastName = lastName;
        this.settings = new Settings(this);

        ++userCounter;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPinNumber() {
        return pinNumber;
    }
}
