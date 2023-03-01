import java.util.Random;

public class User {
    final int userIdLength = 6;
    private int userId;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String pinNumber;
    protected boolean isAdmin;
    protected static Settings settings;

    User(String firstName, String lastName, String userName, String pinNumber,
         boolean isAdmin) {
        this.userId = this.getNewUserId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.pinNumber = pinNumber;
        this.isAdmin = isAdmin;
        settings = new Settings(this);
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

    private int getNewUserId(){
        Random rng = new Random();
        boolean nonUnique;
        String uuid;
        int len = this.userIdLength;

        do {
            uuid = "";
            for (int c = 0 ; c<len; c++) {
                uuid += ((Integer)rng.nextInt(10)).toString();

            }
            nonUnique = false;
            // Check database for userId collision
//             for (User u : this.users) {
//                 if (uuid.compareTo(u.getUserId()) == 0) {
//                     nonUnique = true;
//                     break;
//                 }
//             }

        } while (nonUnique);
        return Integer.parseInt(uuid);
    }
}
