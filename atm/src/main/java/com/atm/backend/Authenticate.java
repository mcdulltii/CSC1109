package com.atm.backend;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.util.Formatter;

public class Authenticate {
    private SQLQueries q;
    private int numTries;

    public Authenticate(Connection conn) {
        this.q = new SQLQueries(conn);
        this.numTries = 0;
    }

    public String hashString(String str, byte[] salt) {
        return encryptSHA256(str, salt);
    }

    public Boolean checkPassword(String username, String password) {
        this.numTries++;
        byte[] passwordSalt = q.getPasswordSaltfromUsername(username);
        return this.hashString(password, passwordSalt).equals(q.getPasswordfromUsername(username));
    }

    public int getNumTries() {
        return this.numTries;
    }

    private String encryptSHA256(String password, byte[] salt) {
        String sha256 = "";
        byte[] passwordStr = {};
        try {
            passwordStr = ByteBuffer.allocate(password.length() + salt.length)
                                            .put(salt)
                                            .put(password.getBytes("UTF-8"))
                                            .array();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Pin number should be in ASCII format.");
        }

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(passwordStr);
            sha256 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to encrypt pin number.");
        }

        return sha256;
    }

    protected byte[] getRandomNonce() {
        byte[] nonce = new byte[16];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    protected byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
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
