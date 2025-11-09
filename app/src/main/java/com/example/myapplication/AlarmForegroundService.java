package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class AlarmForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int requestCode = intent.getIntExtra("requestCode", -1);
        String channelId = "alarm_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

        }
        Intent notificationIntent  = new Intent(this, AlarmStopActivity.class);
        notificationIntent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("requestCode", requestCode);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent ,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("알람 실행 중")
                .setContentText("눌러서 알람을 종료하세요")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(1, notification);
        startForeground(1, notification);

        // 🔹 0.3~0.5초 정도 딜레이 후 StopActivity 실행
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent activityIntent = new Intent(this, AlarmStopActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activityIntent.putExtra("requestCode", requestCode);
            startActivity(activityIntent);
        }, 500);


        // 5초 뒤 자동 종료 로직
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d("AlarmService", "⏰ 3분 경과 — 알람 자동 종료");

            Intent closeIntent = new Intent("com.example.myapplication.ALARM_AUTO_STOP");
            LocalBroadcastManager.getInstance(this).sendBroadcast(closeIntent); // ✅ Local Broadcast

            stopSelf();
        }, 3 * 60 * 1000);


        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE,
                "MyApp::AlarmWakeLock"
        );
        wakeLock.acquire(3000);

//      new Handler(Looper.getMainLooper()).postDelayed(()... 이 구문 사용하려면 아래의 try catch 구문 쓰면 안됨(액티비티 두번 실행됨)

//        try {
//            Intent activityIntent = new Intent(this, AlarmStopActivity.class);
//            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            activityIntent.putExtra("requestCode", requestCode);
//            startActivity(activityIntent);
//            Log.d("AlarmService", "AlarmStopActivity 실행됨");
//        } catch (Exception e) {
//            Log.e("AlarmService", "Activity 실행 실패", e);
//        }

        //stopSelf(); // 서비스 종료
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // 알림(Notification) 제거
        stopForeground(true);  // ✅ 알림바 메시지 제거
        Log.d("AlarmService", "서비스 종료됨 (알림 제거)");
    }
}
