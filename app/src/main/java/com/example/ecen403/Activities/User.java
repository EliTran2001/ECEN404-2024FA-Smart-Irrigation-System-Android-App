package com.example.ecen403.Activities;

public class User {
    private static String name;
    private static String email;
    private static String username;
    private static String password;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public static void setUserData(String name, String email, String username, String password) {
        User.name = name;
        User.email = email;
        User.username = username;
        User.password = password;
    }

    // Getters
    public static String getName() { return name; }
    public static String getEmail() { return email; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}

