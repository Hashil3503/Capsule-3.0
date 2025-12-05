package com.example.myapplication;

import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

/**
 * 전역 Application 클래스
 * - Repository, Room DB 등에서 context 접근
 * - 앱이 백그라운드로 가는 순간 자동 로그아웃 처리
 * - AndroidManifest.xml의 <application>에 반드시 등록 필요
 */
public class MyApplication extends Application implements LifecycleObserver {

    private static MyApplication instance;

    // 전역 인스턴스 반환
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this; // 앱이 실행될 때 단 한 번 초기화

        // ✅ 앱 전체 생명주기 감시 등록
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    // ✅ 앱이 백그라운드로 내려가는 순간 호출됨
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        LoginManager.logout(this); // ✅ 여기서 자동 로그아웃
    }
}
