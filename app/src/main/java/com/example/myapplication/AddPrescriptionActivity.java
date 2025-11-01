package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.internal.service.Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPrescriptionActivity extends AppCompatActivity {

    private static final String TAG = "AddPrescriptionActivity";
    //SQL 쿼리를 위한 각 엔티티의 레파지터리 변수 선언
    private PrescriptionRepository prescriptionRepository;
    private MedicationRepository medicationRepository;
    private Prescription_ViewRepository prescription_viewRepository;
    private MedicineNameRepository medicineNameRepository;
    private List<String> medicineNames = new ArrayList<>(); // 자동완성을 위한 리스트
    private List<MedicineName> nameList = new ArrayList<>(); // 자동완성을 위한 리스트 2
    private List<String> ocrMedicineNames = new ArrayList<>(); //ocr에서 추출한 의약품 이름을 담아두는 리스트

    Calendar regDate;

    private ArrayAdapter<String> adapter; //전역 어댑터 선언

    private String apiKey; //e약은요 apiKey

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_prescription);
        // 레파지터리 객체 생성 및 초기화
        prescriptionRepository = new PrescriptionRepository(getApplication());
        medicationRepository = new MedicationRepository(getApplication());
        prescription_viewRepository = new Prescription_ViewRepository(getApplication());
        medicineNameRepository = new MedicineNameRepository(getApplication());

        apiKey = getString(R.string.med_search_api_key); //e약은요 api 키 가져오기

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Thread(() -> {
            nameList = medicineNameRepository.getAllMedicineNames(); //의약품 이름 목록을 조회해서 medicineNames 문자열 리스트에 추가.

            runOnUiThread(() -> {
                for (MedicineName name : nameList) {
                    medicineNames.add(name.getName());
                }
                // 자동완성을 위한 어댑터 선언
                adapter = new ArrayAdapter<>(
                        AddPrescriptionActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        medicineNames
                );
            });
        }).start();

        LinearLayout medicationContainer = findViewById(R.id.medicationContainer); //의약품 컨테이너 불러오기



        // Result 액티비티에서 넘어온 경우, 의약품 개수 만큼 동적으로 의약품 프레임 생성 후, UI 초기화
        ocrMedicineNames  = getIntent().getStringArrayListExtra("medicine_names"); //Result 액티비티에서 OCR 추출한 의약품 목록 받아오기
        if (ocrMedicineNames  != null && !ocrMedicineNames .isEmpty()) {
            for (String medicineName : ocrMedicineNames ) {

                new Thread(() -> {

                    runOnUiThread(() -> {
                        LayoutInflater inflater = LayoutInflater.from(AddPrescriptionActivity.this); //현재 Activity의 Context를 기반으로 LayoutInflater 객체를 생성.
                        View medicationFrame = inflater.inflate(R.layout.medication_frame, medicationContainer, false); // medication_frame을 메모리에 로드해서 자바에서 사용가능한 View 객체로 변환함.
                        medicationContainer.addView(medicationFrame);

                        AutoCompleteTextView nameEditText = medicationFrame.findViewById(R.id.editmedicationName);
                        nameEditText.setAdapter(adapter);
                        nameEditText.setThreshold(1);
                        nameEditText.setText(medicineName);
                    });

                }).start();

//                // 스크롤이 자동으로 아래로 이동하도록 설정 (이제 필요 없음)
//                final ScrollView scrollView = findViewById(R.id.scrollView); // 스크롤뷰 불러오기
//                scrollView.post(new Runnable() { // UI 업데이트시 동작
//                    @Override
//                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN); //화면을 아래로 스크롤
//                    }
//                });
            }
        }

        // 의약품 프레임 동적 생성 버튼
        Button medicationAdd = findViewById(R.id.button_medicationAdd); //버튼 불러오기

        medicationAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //버튼 누르면 새로운 프레임 생성
                LayoutInflater inflater = LayoutInflater.from(AddPrescriptionActivity.this); //현재 Activity의 Context를 기반으로 LayoutInflater 객체를 생성.
                View medicationFrame = inflater.inflate(R.layout.medication_frame, medicationContainer, false); // medication_frame을 메모리에 로드해서 자바에서 사용가능한 View 객체로 변환함.

                medicationContainer.addView(medicationFrame); //로드한 medication_frame을 medicationContainer에 추가
                //위 메서드들 동작 원리 : 현재 액티비티의 context를 기반으로 LayoutInflater 객체 생성. -> 다른 레이아웃을 inflater 객체로 로드해서 view 객체로 변환 -> 새로만든 view 객체를 현재 레이아웃에 추가.


                AutoCompleteTextView nameEditText = medicationFrame.findViewById(R.id.editmedicationName);
                nameEditText.setAdapter(adapter); //어댑터 연결
                nameEditText.setThreshold(1); // 1글자 입력 시 자동완성 시작


                // 스크롤이 자동으로 아래로 이동하도록 설정
                final ScrollView scrollView = findViewById(R.id.scrollView); // 스크롤뷰 불러오기
                scrollView.post(new Runnable() { // UI 업데이트시 동작
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN); //화면을 아래로 스크롤
                    }
                });
            }
        });

        // 처방전 등록 일자 설정
        regDate = Calendar.getInstance(); //현재 액티비티가 생성되는 시점을 기준으로 날짜/시간이 정해짐.
        Button regDateButton = findViewById(R.id.editRegDate); // 테스트용 기능. 처방전 입력 일자를 수정할 수 있는 기능.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
        regDateButton.setText("등록 일자 :" + sdf.format(regDate.getTime()));
        regDateButton.setOnClickListener(v -> showDatePicker(regDate, regDateButton, "등록 일자: "));

        //UI기반 데이터베이스 쿼리
        Button prescriptionRegister = findViewById(R.id.button_prescriptionRegister);
        prescriptionRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 날짜를 기반으로 Prescription 객체 생성

                EditText durationEditText = findViewById(R.id.editDuration); //복용 일수를 처방전에 저장하기 위해 값 가져오기
                String durationStr = durationEditText.getText().toString().trim();
                int duration = CommonMethod.parseInteger(durationStr);

                if (durationStr.isEmpty() || duration <= 0) { //복용 일수가 없거나 0 이하면 처방전 저장하지 않음.
                    Toast.makeText(AddPrescriptionActivity.this, "복용 일수를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // medicationContainer 내부의 모든 프레임 찾기
                LinearLayout medicationContainer = findViewById(R.id.medicationContainer);
                int count = medicationContainer.getChildCount(); //동적으로 생성된 medication_frame의 갯수

                new Thread(() -> {
                    List<Medication> validMedications = new ArrayList<>();

                    for (int i = 0; i < medicationContainer.getChildCount(); i++) {
                        View frame = medicationContainer.getChildAt(i);
                        AutoCompleteTextView nameEditText = frame.findViewById(R.id.editmedicationName);
                        String name = nameEditText.getText().toString().trim();

                        if (name.isEmpty()) {
                            runOnUiThread(() -> Toast.makeText(AddPrescriptionActivity.this, "빈 의약품 이름이 있습니다", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        // 🔹 네트워크 호출을 백그라운드에서 수행
                        Medication medication = CommonMethod.getDrugInfo(apiKey, name);
                        if (medication != null) validMedications.add(medication);
                    }

                    // 🔹 데이터베이스 삽입 (Room은 자체적으로 백그라운드 동작하긴 하지만 안전하게 Thread 유지)
                    Date selectedDate = regDate.getTime();
                    Prescription prescription = new Prescription(selectedDate);
                    prescription.setDuration(CommonMethod.parseInteger(((EditText)findViewById(R.id.editDuration)).getText().toString()));

                    long prescriptionId = prescriptionRepository.insert(prescription);

                    for (Medication med : validMedications) {
                        long medicationId = medicationRepository.insert(med);
                        Prescription_View relation = new Prescription_View((int) prescriptionId, (int) medicationId);
                        prescription_viewRepository.insert(relation);
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(AddPrescriptionActivity.this, "처방전이 등록되었습니다!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPrescriptionActivity.this, MainActivity.class));
                        finish();
                    });

                }).start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDatePicker(Calendar target, Button button, String prefix) {
        int year = target.get(Calendar.YEAR);
        int month = target.get(Calendar.MONTH);
        int day = target.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            target.set(y, m, d);
            button.setText(prefix + y + "-" + (m + 1) + "-" + d);
        }, year, month, day);

        dialog.show();
    }

}




