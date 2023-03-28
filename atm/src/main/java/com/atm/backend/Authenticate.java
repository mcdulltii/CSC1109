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
    // Number of login tries
    private int numTries;

    public Authenticate(Connection conn) {
        this.q = new SQLQueries(conn);
        this.numTries = 0;
    }

    // Encrypts password string and salt with SHA256
    //
    // # Arguments
    //
    // * `str` - User password
    // * `salt` - Password salt
    //
    // # Return value
    //
    // Encrypted password hash string
    public String hashString(String str, byte[] salt) {
        // Hash input string and salt with SHA256
        return encryptSHA256(str, salt);
    }

    // Checks whether password matches encrypted password
    //
    // # Arguments
    //
    // * `cardNumber` - User card number
    // * `password` - User password
    //
    // # Return value
    //
    // true if password matches encrypted password, else false
    public Boolean checkPassword(String cardNumber, String password) {
        // Increment number of login tries
        this.numTries++;
        // Retrieve password salt and hash input password, then check with database hash for match
        byte[] passwordSalt = q.getPasswordSaltfromCardNumber(cardNumber);
        return this.hashString(password, passwordSalt).equals(q.getPasswordfromCardNumber(cardNumber));
    }

    public int getNumTries() {
        return this.numTries;
    }

    // Encrypts password string and salt with SHA256
    //
    // # Arguments
    //
    // * `password` - User password
    // * `salt` - Password salt
    //
    // # Return value
    //
    // Encrypted password hash string
    private String encryptSHA256(String password, byte[] salt) {
        String sha256 = "";
        byte[] passwordStr = {};
        try {
            // Append password with salt for more randomized hash
            passwordStr = ByteBuffer.allocate(password.length() + salt.length)
                                            .put(salt)
                                            .put(password.getBytes("UTF-8"))
                                            .array();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Pin number should be in ASCII format.");
        }

        try {
            // Encrypt with SHA256
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(passwordStr);
            sha256 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to encrypt pin number.");
        }

        return sha256;
    }

    // Randomly generates a password salt
    //
    // # Return value
    //
    // Password salt of length 16
    protected byte[] getRandomNonce() {
        byte[] nonce = new byte[16];
        // Generate nonce of length 16
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // Randomly generates a password salt
    //
    // # Arguments
    //
    // * `numBytes` - Length of password salt
    //
    // # Return value
    //
    // Password salt of length numBytes
    protected byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        // Generate nonce of length `numBytes`
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // Converts a byte array to a hex string
    //
    // # Arguments
    //
    // * `hash` - Encrypted password hash byte array
    //
    // # Return value
    //
    // Hash string
    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();

        for (byte b : hash) {
            // Format bytes as hex to store as string format
            formatter.format("%02x", b);
        }
        
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
