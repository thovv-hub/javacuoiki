package org.example.cuoikijava.security;

import java.security.MessageDigest;

public class PasswordUtil {

    public static String hashPassword(String password) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder builder = new StringBuilder();
            for(byte b : hash) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }
}