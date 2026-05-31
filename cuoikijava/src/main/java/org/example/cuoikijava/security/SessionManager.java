package org.example.cuoikijava.security;

import org.example.cuoikijava.model.User;

public class SessionManager {

    private static User currentUser;
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isOwner() {
        return currentUser != null && currentUser.getRole().equals("OWNER");
    }


    public static boolean isDriver() {
        return currentUser != null && currentUser.getRole().equals("DRIVER");
    }


    public static boolean isCustomer() {
        return currentUser != null && currentUser.getRole().equals("CUSTOMER");
    }
}