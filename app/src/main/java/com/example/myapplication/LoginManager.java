package com.example.myapplication;

public class LoginManager { //로그인 여부 판별을 위한 싱글톤 메모리 변수 (앱 종료시에 자동으로 초기화)
    private static boolean isLoggedIn = false;

    public static void login() {
        isLoggedIn = true;
    }

    public static void logout() {
        isLoggedIn = false;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
}
