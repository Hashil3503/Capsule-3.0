package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    private MedicineTableRepository medicineTableRepository;
    private DatabaseHelper dbHelper;

    private MedicineNameRepository medicineNameRepository;

    private List<MedicineName> nameListCheck = new ArrayList<>(); // 기존 의약품 이름 목록 확인을 위한 리스트

    // 의약품 이름 목록 추가가 제대로 되었는지 확인하기 위한 리스트들
    private List<MedicineName> nameList = new ArrayList<>();
    private List<String> medicineNames = new ArrayList<>();

    private FrameLayout loadingOverlay;

    private LinearLayout buttonOCR, buttonViewPrescription, buttonBloodMenu, buttonAllAlarmList, buttonChatBot,
            buttonSetting, // 설정 버튼
            buttonMedSearch; // buttonMedSearch는 약품 성분 조회 api 테스트용

    private boolean DBOK; //데이터베이스 로딩 끝났는지 판별을 위한 불린 자료형
    
    private int infocount = 0; //의약품 정보 몇개 저장했는지 확인용


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());

        medicineTableRepository = new MedicineTableRepository(getApplication());

        medicineNameRepository = new MedicineNameRepository(getApplication());

        DBOK = false;

        // 자동완성을 위한 의약품 이름 목록 데이터베이스 초기화
        MakeNameList();


        //UI 요소 찾기
        loadingOverlay = findViewById(R.id.loadingOverlay);
        buttonOCR = findViewById(R.id.button_OCR);
        buttonViewPrescription = findViewById(R.id.button_ViewPrescription);
        buttonBloodMenu = findViewById(R.id.button_BloodMenu);
        buttonAllAlarmList = findViewById(R.id.button_AllAlarmList);
        buttonChatBot = findViewById(R.id.button_ChatBot);
        buttonSetting = findViewById(R.id.button_setting);// 설정 버튼
        buttonMedSearch = findViewById(R.id.button_search_test);// 약품 성분 조회 api 테스트용

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

        buttonBloodMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BloodMenuActivity.class);
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

        buttonMedSearch.setOnClickListener(new View.OnClickListener() { //테스트용. 추후 삭제 예정
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MedSearchActivity.class);
                startActivity(intent);
            }
        });


    }

    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // 모든 액티비티 종료
    }

    private void saveMedicineInfo(String medicineName, String ingredient, String effect, String form, String precaution) {
        if (medicineTableRepository.getMedicationByName(medicineName) != null) {
            Log.d(TAG, "중복된 약품 발견: " + medicineName + ", 저장 생략.");
            return;  // 중복된 약품명이 있으면 삽입하지 않고 종료
        }

        MedicineTable medicine = new MedicineTable();
        medicine.setName(medicineName);
        medicine.setIngredients(ingredient);
        medicine.setEffects(effect);
        medicine.setAppearance(form);
        medicine.setCaution(precaution);
        medicine.setMemo("");
        medicine.setSideeffct("");
        medicine.setSe_existence(false);

        medicineTableRepository.insert(medicine);
        infocount++;
        Log.d(TAG, "약품 정보 저장 완료: " + medicineName + " 총 " + infocount + "개");
//        Log.d(TAG, "=== 저장된 약품 정보 ===");
//        Log.d(TAG, "이름: " + medicineName);
//        Log.d(TAG, "성분: " + ingredient);
//        Log.d(TAG, "효과: " + effect);
//        Log.d(TAG, "제형: " + form);
//        Log.d(TAG, "주의사항: " + precaution);
//        Log.d(TAG, "총 저장된 수: " + infocount);

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
            if(nameListCheck != null && !nameListCheck.isEmpty()){
                Log.d(TAG, "자동완성 데이터베이스가 이미 생성됨.");
                runOnUiThread(() -> Toast.makeText(this, "자동 완성 데이터베이스 준비 완료", Toast.LENGTH_SHORT).show());
                DBOK = true;
                checkLoadingDone();
                return;
            }
            else {
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

    private void checkLoadingDone() { //두 데이터베이스가 모두 초기화 되었는지 체크하기 위한 메서드
        if (DBOK) {
            runOnUiThread(() -> {
                loadingOverlay.setVisibility(View.GONE);
            });
        }
    }

}
