package com.atm.backend;

public class AccUserObj {
    private Account account;
    private User user;

    public AccUserObj (Account acc, User user) {
        this.account = acc;
        this.user = user;
    }

    // Get account details from database
    //
    // # Return value
    //
    // Account object
    public Account getAccount() {
        return this.account;
    }
    
    // Get user details from database
    //
    // # Return value
    //
    // User object
    public User getUser() {
        return this.user;
    }
}