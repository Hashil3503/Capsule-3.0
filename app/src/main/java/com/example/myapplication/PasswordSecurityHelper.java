package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordSecurityHelper {

    private static final String PREF_NAME = "secure_password_store";
    private static final String KEY_HASH = "password_hash";

    private SharedPreferences prefs;

    public PasswordSecurityHelper(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (Exception e) {
            throw new RuntimeException("EncryptedSharedPreferences init error", e);
        }
    }

    // SHA-256 해싱 함수
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : digest)
                sb.append(String.format("%02x", b));

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 해시값 저장
    public void savePasswordHash(String hash) {
        prefs.edit().putString(KEY_HASH, hash).apply();
    }

    // 저장된 해시 가져오기
    public String getPasswordHash() {
        return prefs.getString(KEY_HASH, null);
    }

    // 비밀번호 검증
    public boolean checkPassword(String plainInput) {
        String savedHash = getPasswordHash();
        if (savedHash == null) return false;

        String inputHash = sha256(plainInput);
        return savedHash.equals(inputHash);
    }
}
