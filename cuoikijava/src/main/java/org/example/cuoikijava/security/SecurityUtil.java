package org.example.cuoikijava.security;

public class SecurityUtil {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isValidPhone(String phone) {
        return phone.matches("0\\d{9}");
    }

    public static boolean isStrongPassword(String password) {
        return password.length() >= 3;
    }


    public static boolean isNumber(String text) {

        try {
            Integer.parseInt(text);
            return true;
        }

        catch (Exception e) {
            return false;
        }
    }
}