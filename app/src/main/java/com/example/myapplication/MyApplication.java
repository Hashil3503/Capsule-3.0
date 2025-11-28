package com.example.myapplication;

import android.app.Application;

/**
 * 전역 Application 클래스
 * - Repository, Room DB 등에서 context 접근을 쉽게 하기 위해 사용
 * - 반드시 AndroidManifest.xml의 <application>에 등록 필요
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    // 전역 인스턴스 반환
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this; // 앱이 실행될 때 단 한 번 초기화
    }
}
