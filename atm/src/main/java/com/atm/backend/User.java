package com.atm.backend;

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

    protected String getFirstName() {
        return this.firstName;
    }

    protected String getLastName() {
        return this.lastName;
    }

    protected String getPin() {
        return this.pinNumber;
    }

    protected void setPin(String pin) {
        this.pinNumber = pin;
    }

    protected int getIsAdmin() {
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
        } while (nonUnique);
        return Integer.parseInt(uuid);
    }
}
