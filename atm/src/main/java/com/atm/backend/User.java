package com.atm.backend;

import java.sql.Connection;
import java.util.Random;

public class User {
    final int userIdLength = 6;
    private int userId;
    private String AccNo;
    private String firstName;
    private String lastName;
    private String pinNumber;
    private int isAdmin;

    public User(String AccNo, String firstName, String lastName, int isAdmin) {
        this.AccNo = AccNo;
        this.userId = this.getNewUserId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinNumber = "";
        this.isAdmin = isAdmin;
    }

    protected int getUserId() {
        return this.userId;
    }

    protected String getAccNo() {
        return this.AccNo;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    protected String getPin() {
        return this.pinNumber;
    }

    public void setPin(String pin, Connection conn) {
        Authenticate auth = new Authenticate(conn);
        SQLQueries q = new SQLQueries();
        byte[] passwordSalt = q.executeQuerySettings(this, "salt");
        this.pinNumber = auth.hashString(pin, passwordSalt);
    }

    public int getIsAdmin() {
        return this.isAdmin;
    }

    private int getNewUserId() {
        Random rng = new Random();
        boolean nonUnique;
        String uuid;
        int len = this.userIdLength;

        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer) rng.nextInt(10)).toString();

            }
            nonUnique = false;
            // Check database for userId collision
            // for (User u : this.users) {
            // if (uuid.compareTo(u.getUserId()) == 0) {
            // nonUnique = true;
            // break;
            // }
            // }

        } while (nonUnique);
        return Integer.parseInt(uuid);
    }
}
