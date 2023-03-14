package com.atm.backend;

public class AccUserObj {
    private Account account;
    private User user;

    public AccUserObj (Account acc, User user) {
        this.account = acc;
        this.user = user;
    }

    public Account getAccount() {
        return this.account;
    }
    
    public User getUser() {
        return this.user;
    }
}