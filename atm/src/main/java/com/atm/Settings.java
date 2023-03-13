package com.atm;

public class Settings implements UserSettings, AccountSettings {
    private Account account;
    private User user;
    SQLQueries q = new SQLQueries();

    Settings(Account account) {
        this.account = account;
    }

    Settings(User user) {
        this.user = user;
    }

    public void setPinNumber(String pinNumber) {
        user.setPin(pinNumber);
        q.executeQuerySettings(user, "pin");
    }

    public void setTransferLimit(double limit) {
        account.setTransferLimit(limit);
        q.executeQuerySettings(account, "transferlimit");
    }
}
