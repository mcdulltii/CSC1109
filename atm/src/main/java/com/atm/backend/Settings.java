package com.atm.backend;

import java.sql.Connection;

public class Settings implements UserSettings, AccountSettings {
    private Account account;
    private Authenticate auth;
    private Connection conn;
    private User user;
    private SQLQueries q;

    public Settings(Account account, Connection conn) {
        this.account = account;
        setFields(conn);
    }

    public Settings(User user, Connection conn) {
        this.user = user;
        setFields(conn);
    }

    private void setFields(Connection conn) {
        this.auth = new Authenticate(conn);
        this.conn = conn;
        this.q = new SQLQueries(this.conn);
    }

    public void setPinNumber(String pinNumber) {
        byte[] passwordSalt = auth.getRandomNonce();
        user.setPin(auth.hashString(pinNumber, passwordSalt));
        q.executeQuerySettings(user, "pin");
        q.executeQuerySettings(user, "salt", passwordSalt);
    }

    public void setTransferLimit(double limit) {
        account.setTransferLimit(limit);
        q.executeQuerySettings(account, "transferlimit");
    }
}
