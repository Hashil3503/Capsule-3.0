package com.example.myapplication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmStopActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm_stop);

        // 알람 소리 설정
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        try {
            // ✅ MediaPlayer 생성
            MediaPlayer mediaPlayer = new MediaPlayer();

            // ✅ 스트림 타입: STREAM_ALARM (시계앱과 동일 채널)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

            // ✅ 데이터 소스 설정
            mediaPlayer.setDataSource(this, alarmUri);

            // ✅ 루프 재생 (알람 계속 울림)
            mediaPlayer.setLooping(true);

            // ✅ 볼륨 자동 최대 설정 (선택)
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            }

            // ✅ 준비 후 재생
            mediaPlayer.prepare();
            mediaPlayer.start();

            // 🔹 액티비티 종료 시에 알람 멈추기 위해 멤버변수로 저장
            this.mediaPlayer = mediaPlayer;

        } catch (Exception e) {
            e.printStackTrace();
        }

        //알람 객체 불러오기
        int requestCode = getIntent().getIntExtra("requestCode", -1);

        SharedPreferences prefs = getSharedPreferences("alarms", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("alarm_list", null);
        Type type = new TypeToken<ArrayList<AlarmItem>>() {}.getType();
        ArrayList<AlarmItem> alarmList = gson.fromJson(json, type);

        TextView textView = findViewById(R.id.prescription);
        if (alarmList != null) {
            for (AlarmItem item : alarmList) {
                if (item.getRequestCode() == requestCode) {
                    textView.setText("처방전" + item.getPid()); // pid 값을 텍스트로 표시
                    break;
                }
            }
        }

        Button btnStop = findViewById(R.id.btnStopAlarm);
        btnStop.setOnClickListener(v -> {
            // 🔸 복용 완료 표시
            if (alarmList != null) {
                for (AlarmItem item : alarmList) {
                    if (item.getRequestCode() == requestCode) {
                        item.setTaken(true); // 복용 완료 상태로 변경
                        break;
                    }
                }

                // 다시 저장
                String updatedJson = gson.toJson(alarmList);
                prefs.edit().putString("alarm_list", updatedJson).apply();


            }
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // 알람 서비스 종료 안될 경우 대비 Notification 먼저 제거
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(1); // startForeground(1, notification)의 ID와 동일
            // 알람 서비스 종료
            stopService(new Intent(getApplicationContext(), AlarmForegroundService.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.example.myapplication.ALARM_AUTO_STOP");
        LocalBroadcastManager.getInstance(this).registerReceiver(autoStopReceiver, filter);
        Log.d("AlarmStopActivity", "LocalBroadcastReceiver 등록 완료");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(autoStopReceiver);
        Log.d("AlarmStopActivity", "LocalBroadcastReceiver 해제됨");
    }

    private final BroadcastReceiver autoStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AlarmStopActivity", "서비스에서 자동 종료 신호 수신됨");
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
            finish();
        }
    };


    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 터치해도 아무 동작도 하지 않도록 하기 위함. (뒤로가기로 알람 화면을 나가면 알람이 정상 종료 되지 않아서 의도되지 않은 버그가 발생함.
    }
}
