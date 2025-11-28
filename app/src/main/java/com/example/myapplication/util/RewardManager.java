package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

public class RewardManager {
    private static final String PREF = "rewards_pref";
    private static final String KEY_BALANCE = "balance";

    public static int getBalance(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getInt(KEY_BALANCE, 0);
    }

    public static void add(Context ctx, int amount) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_BALANCE, getBalance(ctx) + amount).apply();
    }

    public static boolean spend(Context ctx, int amount) {
        int cur = getBalance(ctx);
        if (cur < amount) return false;
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_BALANCE, cur - amount).apply();
        return true;
    }

    public static void refund(Context ctx, int amount) {
        add(ctx, amount);
    }

    public static void set(Context ctx, int amount) {
        if (amount < 0) amount = 0;
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putInt(KEY_BALANCE, amount).apply();
    }
    public static void reset(Context ctx) { set(ctx, 0); }


}
