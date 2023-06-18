package com.example.project;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * hashing the password of user
 */
public class HashingPassword {
    /**
     * encrypt the original password to prevent from cyber attacks
     * @param password - get the original password
     * @return - encrypt password
     */
    public static String hashPassword(String password) {
        try {
            // Get an instance of the SHA-256 message digest algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hash the password
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert the hash to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Return the hex string
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}