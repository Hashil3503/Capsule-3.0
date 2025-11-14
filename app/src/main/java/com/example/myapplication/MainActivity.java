package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DatabaseHelper dbHelper;

    private MedicineNameRepository medicineNameRepository;

    private List<MedicineName> nameListCheck = new ArrayList<>(); // 기존 의약품 이름 목록 확인을 위한 리스트

    // 의약품 이름 목록 추가가 제대로 되었는지 확인하기 위한 리스트들
    private List<MedicineName> nameList = new ArrayList<>();
    private List<String> medicineNames = new ArrayList<>();

    private FrameLayout loadingOverlay;

    // ⬇️ buttonMedSearch 제거
    private LinearLayout buttonOCR, buttonViewPrescription, buttonAllAlarmList, buttonChatBot, buttonSetting; // 설정 버튼

    private boolean DBOK; //데이터베이스 로딩 끝났는지 판별을 위한 불린 자료형

    private int infocount = 0; //의약품 정보 몇개 저장했는지 확인용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());

        medicineNameRepository = new MedicineNameRepository(getApplication());

        showPasswordDialog();

        DBOK = false;

        // 자동완성을 위한 의약품 이름 목록 데이터베이스 초기화
        MakeNameList();

        // UI 요소 찾기
        loadingOverlay = findViewById(R.id.loadingOverlay);
        buttonOCR = findViewById(R.id.button_OCR);
        buttonViewPrescription = findViewById(R.id.button_ViewPrescription);
        buttonAllAlarmList = findViewById(R.id.button_AllAlarmList);
        buttonChatBot = findViewById(R.id.button_ChatBot);
        buttonSetting = findViewById(R.id.button_Settings);

        loadingOverlay.setVisibility(View.VISIBLE); // 데이터베이스 초기화 작업 완료까지 보여줄 로딩 바

        buttonOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OCRActivity.class);
                startActivity(intent);
            }
        });

        buttonViewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewPrescriptionActivity.class);
                startActivity(intent);
            }
        });

        buttonAllAlarmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllAlarmListActivity.class);
                startActivity(intent);
            }
        });

        buttonChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatBotActivity.class);
                startActivity(intent);
            }
        });

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // 모든 액티비티 종료
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }

    private void MakeNameList() {
        new Thread(() -> {
            nameListCheck = medicineNameRepository.getAllMedicineNames();
            if (nameListCheck != null && !nameListCheck.isEmpty()) {
                Log.d(TAG, "자동완성 데이터베이스가 이미 생성됨.");
                runOnUiThread(() -> Toast.makeText(this, "자동 완성 데이터베이스 준비 완료", Toast.LENGTH_SHORT).show());
                DBOK = true;
                checkLoadingDone();
                return;
            } else {
                try {
                    String fileName = "약품명목록.csv";
                    InputStream inputStream = getAssets().open(fileName);

                    if (inputStream == null) {
                        Log.e(TAG, "CSV 파일을 열 수 없습니다: " + fileName);
                        runOnUiThread(() -> Toast.makeText(this, "의약품 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // CSV 파일을 DataTable로 변환
                    List<Map<String, String>> dataTable = convertCsvToDataTable(inputStream);
                    Log.d(TAG, "CSV 파일 로드 완료: " + dataTable.size() + "개의 행");

                    if (dataTable.isEmpty()) {
                        Log.e(TAG, "CSV 파일에 데이터가 없습니다.");
                        runOnUiThread(() -> Toast.makeText(this, "의약품 데이터가 비어있습니다.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // '제품명' 열에 해당하는 목록을 추출
                    List<String> productNames = extractProductNames(dataTable);
                    Log.d(TAG, "제품명 추출 완료: " + productNames.size() + "개의 제품명");

                    if (productNames.isEmpty()) {
                        Log.e(TAG, "추출된 제품명이 없습니다.");
                        runOnUiThread(() -> Toast.makeText(this, "제품명을 추출할 수 없습니다.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    medicineNameRepository.insertAll(productNames);
                    Log.d(TAG, "데이터베이스 생성 완료");
                    runOnUiThread(() -> Toast.makeText(this, "자동완성 데이터베이스가 생성되었습니다.", Toast.LENGTH_SHORT).show());
                    DBOK = true;
                    checkLoadingDone();

                    // 데이터베이스에 내용이 제대로 추가되었는지 확인하기 위한 디버깅용 코드
                    nameList = medicineNameRepository.getAllMedicineNames();
                    for (MedicineName name : nameList) {
                        medicineNames.add(name.getName());
                    }

                    Log.i(TAG, String.format("의약품 데이터베이스 로드 완료: %d개의 제품명", medicineNames.size()));
                    // 로드된 약품명 샘플 출력
                    int sampleCount = Math.min(10, medicineNames.size());
                    Log.d(TAG, "로드된 약품명 샘플:");
                    for (int i = 0; i < sampleCount; i++) {
                        Log.d(TAG, "- " + medicineNames.get(i));
                    }

                } catch (IOException e) {
                    Log.e(TAG, "CSV 파일 로드 실패", e);
                    runOnUiThread(() -> Toast.makeText(this, "의약품 데이터베이스 로드에 실패했습니다.", Toast.LENGTH_SHORT).show());
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "데이터베이스 처리 중 오류 발생", e);
                    runOnUiThread(() -> Toast.makeText(this, "데이터베이스 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                    finish();
                }
            }
        }).start();
    }

    private List<Map<String, String>> convertCsvToDataTable(InputStream inputStream) throws IOException {
        List<Map<String, String>> dataTable = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "EUC-KR"));

        // 헤더(열 이름) 읽기
        String headerLine = reader.readLine();
        if (headerLine == null) {
            reader.close();
            return dataTable;
        }

        // 쉼표로 구분하되, 따옴표 안의 쉼표는 무시
        String[] headers = headerLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim().replaceAll("\"", "");
        }

        Log.d(TAG, "CSV 헤더: " + Arrays.toString(headers));

        // 데이터 행 읽기
        String line;
        int rowCount = 0;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Map<String, String> row = new HashMap<>();

            // 각 열의 값을 매핑
            for (int i = 0; i < Math.min(headers.length, values.length); i++) {
                row.put(headers[i], values[i].trim().replaceAll("\"", ""));
            }

            dataTable.add(row);
            rowCount++;

            if (rowCount % 1000 == 0) {
                Log.d(TAG, "CSV 파일 처리 중: " + rowCount + "행");
            }
        }

        reader.close();
        Log.d(TAG, "CSV 파일 처리 완료: 총 " + rowCount + "행");
        return dataTable;
    }

    private List<String> extractProductNames(List<Map<String, String>> dataTable) {
        List<String> productNames = new ArrayList<>();

        for (Map<String, String> row : dataTable) {
            // '제품명' 열의 값을 찾아 리스트에 추가
            String productName = row.get("제품명");
            if (productName != null && !productName.trim().isEmpty()) {
                productNames.add(productName.trim());
            }
        }

        return productNames;
    }

    private void checkLoadingDone() { //데이터베이스가 모두 초기화 되었는지 체크하기 위한 메서드
        if (DBOK) {
            runOnUiThread(() -> {
                loadingOverlay.setVisibility(View.GONE);
            });
        }
    }

    private void showPasswordDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_password_window);
        dialog.setCancelable(false);

        EditText inputPassword = dialog.findViewById(R.id.inputPassword);
        TextView textWarning = dialog.findViewById(R.id.textWarning);
        Button loginBtn = dialog.findViewById(R.id.loginButton);
        Button cancelBtn = dialog.findViewById(R.id.cancelButton);

        PasswordSecurityHelper helper = new PasswordSecurityHelper(this);
        String savedHash = helper.getPasswordHash();

        textWarning.setVisibility(View.INVISIBLE);

        // 만약 비번이 저장된 적이 없다면 → 처음 실행 → PW 설정 모드로 전환
        if (savedHash == null) {
            textWarning.setText("처음 사용하는 경우 비밀번호를 설정하세요.");
            textWarning.setVisibility(View.VISIBLE);
            loginBtn.setText("설정");
        }

        loginBtn.setOnClickListener(v -> {
            String pwInput = inputPassword.getText().toString();

            // 최소 길이 체크 (신규 PW 설정 또는 변경 시)
            if (pwInput.length() < 8) {
                textWarning.setText("비밀번호는 최소 8자리 이상이어야 합니다.");
                textWarning.setVisibility(View.VISIBLE);
                return;  // 여기서 종료 → PW 저장하지 않음
            }

            if (savedHash == null) {
                // 첫 비밀번호 설정
                helper.savePasswordHash(PasswordSecurityHelper.sha256(pwInput));
                dialog.dismiss();
                showPasswordDialog();
            } else {
                // 기존 비밀번호 검증
                if (helper.checkPassword(pwInput)) {
                    dialog.dismiss();
                } else {
                    textWarning.setText("비밀번호가 틀렸습니다.");
                    textWarning.setVisibility(View.VISIBLE);
                }
            }
        });

        cancelBtn.setOnClickListener(v -> {
            // 로그인 취소 = 앱 종료
            finish();
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


}
