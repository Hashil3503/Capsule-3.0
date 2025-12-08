package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppTaskService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // ✅ 최근 앱에서 제거됨 → 여기서 로그아웃 실행됨
        LoginManager.logout(getApplicationContext());

        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
