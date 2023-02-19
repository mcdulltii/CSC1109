public class User {
    // potential temp method for userId
    static int userCounter = 0;

    private int userId;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String pinNumber;
    protected boolean isAdmin;
    protected static Settings settings;

    User(String firstName, String lastName, String userName, String pinNumber,
         boolean isAdmin) {
        this.userId = userCounter;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.pinNumber = pinNumber;
        this.isAdmin = isAdmin;
        settings = new Settings(this);

        ++userCounter;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
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
