package com.example.myapplication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class SetDefaultAlarmActivity extends AppCompatActivity {

    private DefaultAlarmSetRepository defaultAlarmSetRepository;
    Button btnTime1, btnTime2, btnTime3, btnTime4, btnTime5, btnReset ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_default_alarm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        defaultAlarmSetRepository = new DefaultAlarmSetRepository(getApplication());

        btnTime1 = findViewById(R.id.btnTime1);
        btnTime2 = findViewById(R.id.btnTime2);
        btnTime3 = findViewById(R.id.btnTime3);
        btnTime4 = findViewById(R.id.btnTime4);
        btnTime5 = findViewById(R.id.btnTime5);
        btnReset = findViewById(R.id.btnReset);


        btnTime1.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {
                        // 선택한 시간으로 버튼 텍스트 변경 (예: "09:30" 형식)
                        String timeText = String.format("아침 : %02d:%02d", selectedHour, selectedMinute);
                        btnTime1.setText(timeText);

                        // 데이터베이스에 선택한 시간 저장 (1번 레코드)
                        DefaultAlarmSet defaultAlarmSet = new DefaultAlarmSet(1, selectedHour, selectedMinute);

                        // Room DB는 메인스레드 접근 불가 → 별도 스레드로 실행
                        new Thread(() -> {
                            defaultAlarmSetRepository.insert(defaultAlarmSet);
                        }).start();
                    },
                    hour,
                    minute,
                    true
            );


            dialog.show();
        });

        btnTime2.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {
                        // 선택한 시간으로 버튼 텍스트 변경 (예: "09:30" 형식)
                        String timeText = String.format("점심 : %02d:%02d", selectedHour, selectedMinute);
                        btnTime2.setText(timeText);

                        // 데이터베이스에 선택한 시간 저장 (1번 레코드)
                        DefaultAlarmSet defaultAlarmSet = new DefaultAlarmSet(2, selectedHour, selectedMinute);

                        // Room DB는 메인스레드 접근 불가 → 별도 스레드로 실행
                        new Thread(() -> {
                            defaultAlarmSetRepository.insert(defaultAlarmSet);
                        }).start();
                    },
                    hour,
                    minute,
                    true
            );


            dialog.show();
        });

        btnTime3.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {
                        // 선택한 시간으로 버튼 텍스트 변경 (예: "09:30" 형식)
                        String timeText = String.format("저녁 : %02d:%02d", selectedHour, selectedMinute);
                        btnTime3.setText(timeText);

                        // 데이터베이스에 선택한 시간 저장 (1번 레코드)
                        DefaultAlarmSet defaultAlarmSet = new DefaultAlarmSet(3, selectedHour, selectedMinute);

                        // Room DB는 메인스레드 접근 불가 → 별도 스레드로 실행
                        new Thread(() -> {
                            defaultAlarmSetRepository.insert(defaultAlarmSet);
                        }).start();
                    },
                    hour,
                    minute,
                    true
            );


            dialog.show();
        });

        btnTime4.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {
                        // 선택한 시간으로 버튼 텍스트 변경 (예: "09:30" 형식)
                        String timeText = String.format("기타1 : %02d:%02d", selectedHour, selectedMinute);
                        btnTime4.setText(timeText);

                        // 데이터베이스에 선택한 시간 저장 (1번 레코드)
                        DefaultAlarmSet defaultAlarmSet = new DefaultAlarmSet(4, selectedHour, selectedMinute);

                        // Room DB는 메인스레드 접근 불가 → 별도 스레드로 실행
                        new Thread(() -> {
                            defaultAlarmSetRepository.insert(defaultAlarmSet);
                        }).start();
                    },
                    hour,
                    minute,
                    true
            );


            dialog.show();
        });

        btnTime5.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, selectedHour, selectedMinute) -> {
                        // 선택한 시간으로 버튼 텍스트 변경 (예: "09:30" 형식)
                        String timeText = String.format("기타2 : %02d:%02d", selectedHour, selectedMinute);
                        btnTime5.setText(timeText);

                        // 데이터베이스에 선택한 시간 저장 (1번 레코드)
                        DefaultAlarmSet defaultAlarmSet = new DefaultAlarmSet(5, selectedHour, selectedMinute);

                        // Room DB는 메인스레드 접근 불가 → 별도 스레드로 실행
                        new Thread(() -> {
                            defaultAlarmSetRepository.insert(defaultAlarmSet);
                        }).start();
                    },
                    hour,
                    minute,
                    true
            );


            dialog.show();
        });

        btnReset.setOnClickListener(v -> {
            new Thread(() -> {
                defaultAlarmSetRepository.deleteAll();
                runOnUiThread(() -> {
                    btnTime1.setText("아침");
                    btnTime2.setText("점심");
                    btnTime3.setText("저녁");
                    btnTime4.setText("기타1");
                    btnTime5.setText("기타2");

                    Toast.makeText(this, "기본 알람 시간이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                });
            }).start();


        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                DefaultAlarmSet alarmSet = defaultAlarmSetRepository.getById(i);

                if (alarmSet != null) {
                    int hour = alarmSet.getHour();
                    int min = alarmSet.getMin();
                    String label;

                    switch (i) {
                        case 1: label = "아침"; break;
                        case 2: label = "점심"; break;
                        case 3: label = "저녁"; break;
                        case 4: label = "기타1"; break;
                        case 5: label = "기타2"; break;
                        default: label = "시간"; break;
                    }

                    String timeText = String.format("%s : %02d:%02d", label, hour, min);

                    int finalI = i;
                    runOnUiThread(() -> {
                        switch (finalI) {
                            case 1: btnTime1.setText(timeText); break;
                            case 2: btnTime2.setText(timeText); break;
                            case 3: btnTime3.setText(timeText); break;
                            case 4: btnTime4.setText(timeText); break;
                            case 5: btnTime5.setText(timeText); break;
                        }
                    });
                }
            }
        }).start();
    }

}
