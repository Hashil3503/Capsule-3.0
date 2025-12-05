package com.example.myapplication;

import android.content.Context;

public class LoginManager {

    private static final String PREF_NAME = "login_pref";
    private static final String KEY_LOGGED_IN = "key_logged_in";

    public static void login(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public static void logout(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .apply();
    }

    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGGED_IN, false);
    }
}

