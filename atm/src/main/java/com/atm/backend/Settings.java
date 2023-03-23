package com.atm.backend;

import java.sql.Connection;

public class Settings implements UserSettings, AccountSettings {
    private Account account;
    private Connection conn;
    private User user;
    private SQLQueries q;

    public Settings(Account account, Connection conn) {
        this.account = account;
        this.conn = conn;
        this.q = new SQLQueries(conn);
    }

    public Settings(User user, Connection conn) {
        this.user = user;
        this.conn = conn;
        this.q = new SQLQueries(conn);
    }

    public void setPinNumber(String pinNumber) {
        user.setPin(pinNumber, conn);
        q.executeQuerySettings(user, "pin");
    }

    public void setTransferLimit(double limit) {
        account.setTransferLimit(limit);
        q.executeQuerySettings(account, "transferlimit");
    }
}
