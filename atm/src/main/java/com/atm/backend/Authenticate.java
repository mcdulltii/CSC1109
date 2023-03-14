package com.atm.backend;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Authenticate {
    private SQLQueries q;
    private int numTries;

    public Authenticate() {
        this.q = new SQLQueries();
        this.numTries = 0;
    }

    protected String hashString(String str) {
        return encryptSHA256(str);
    }

    public Boolean checkPassword(String username, String password) {
        this.numTries++;
        return this.hashString(password).equals(q.getPasswordfromUsername(username));
    }

    public int getNumTries() {
        return this.numTries;
    }

    private String encryptSHA256(String password) {
        String sha256 = "";

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha256 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sha256;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();

        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}