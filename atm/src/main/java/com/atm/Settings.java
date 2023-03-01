package com.atm;
public class Settings implements UserSettings, AccountSettings {
    private User user;
    private Account account;

    Settings(User user) {
        this.user = user;
    }

    Settings(Account account) {
        this.account = account;
    }

    public void setFirstName(String firstName) {
        user.firstName = firstName;
    }

    public void setLastName(String lastName) {
        user.lastName = lastName;
    }

    public void setUserName(String userName) {
        user.userName = userName;
    }

    public void setPinNumber(String pinNumber) {
        user.pinNumber = pinNumber;
    }

    public void setTransferLimit(double limit) {
        account.transferLimit = limit;
    }
}
