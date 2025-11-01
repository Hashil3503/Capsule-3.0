package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewDetailActivity extends AppCompatActivity {

    private static final String TAG = "ViewDetailActivity";
    private PrescriptionRepository prescriptionRepository;
    private MedicationRepository medicationRepository;
    private Prescription_ViewRepository prescription_viewRepository;

    private MedicineNameRepository medicineNameRepository;

    private List<String> medicineNames = new ArrayList<>(); // 자동완성을 위한 리스트
    private List<MedicineName> nameList = new ArrayList<>(); // 자동완성을 위한 리스트 2
    private ArrayAdapter<String> adapter;

    private String apiKey; //e약은요 apiKey

    private boolean se_existence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_detail);

        prescriptionRepository = new PrescriptionRepository(getApplication());
        medicationRepository = new MedicationRepository(getApplication());
        prescription_viewRepository = new Prescription_ViewRepository(getApplication());
        medicineNameRepository = new MedicineNameRepository(getApplication());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiKey = getString(R.string.med_search_api_key); //e약은요 api 키 가져오기

        new Thread(() -> {
            nameList = medicineNameRepository.getAllMedicineNames(); //의약품 이름 목록을 조회해서 medicineNames 문자열 리스트에 추가.

            runOnUiThread(() -> {
                for (MedicineName name : nameList) {
                    medicineNames.add(name.getName());
                }
                // 자동완성을 위한 어댑터 선언
                adapter = new ArrayAdapter<>(
                        ViewDetailActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        medicineNames
                );
            });
        }).start();



        long prescriptionId = getIntent().getLongExtra("prescription_id", -1);

        LinearLayout medicationContainer = findViewById(R.id.medicationContainer);
        Button prescriptiondeleteButton = findViewById(R.id.delete);

        if (prescriptionId != -1) {
            new Thread(() -> {
                List<View> medicationFrames = new ArrayList<>();
                List<Prescription_View> prescriptionViews = prescription_viewRepository.getMedicationsForPrescription(prescriptionId);

                TextView viewprescriptionId = findViewById(R.id.prescriptionId);
                TextView prescriptionDate = findViewById(R.id.prescriptionDate);
                TextView prescriptionDuration = findViewById(R.id.prescriptionDuration);
                Prescription prescription = prescriptionRepository.getPrescriptionById(prescriptionId);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREAN); // prescriptionDate에 날짜 형식을 나타내기 전에 내가 원하는 포맷으로 설정.

                viewprescriptionId.setText("처방전 ID: " + prescriptionId);
                prescriptionDate.setText("등록 일자: " + sdf.format(prescription.getDate()));
                prescriptionDuration.setText("복용 기간: " + prescription.getDuration());

                for (Prescription_View prescriptionView : prescriptionViews) {
                    long medicationId = prescriptionView.getMedication_id();
                    Medication medication = medicationRepository.getMedicationById(medicationId);
                    String norName = CommonMethod.normalizeWord(medication.getItemName());

                    if (medication != null) {
                        LayoutInflater inflater = LayoutInflater.from(this);
                        View medicationFrame = inflater.inflate(R.layout.medication_view_frame, medicationContainer, false);

                        TextView Text1 = medicationFrame.findViewById(R.id.medicationName);
                        TextView nameText = medicationFrame.findViewById(R.id.viewmedicationName);
                        TextView Text2 = medicationFrame.findViewById(R.id.medicationEntpName);
                        TextView entpNameText = medicationFrame.findViewById(R.id.viewmedicationEntpName);
                        TextView Text3 = medicationFrame.findViewById(R.id.medicationItemSeq);
                        TextView itemSeqText = medicationFrame.findViewById(R.id.viewmedicationItemSeq);
                        TextView Text4 = medicationFrame.findViewById(R.id.medicationEfcyQesitm);
                        TextView efcyQesitmText = medicationFrame.findViewById(R.id.viewmedicationEfcyQesitm);
                        TextView Text5 = medicationFrame.findViewById(R.id.medicationUseMethodQesitm);
                        TextView useMethodQesitmText = medicationFrame.findViewById(R.id.viewmedicationUseMethodQesitm);
                        TextView Text6 = medicationFrame.findViewById(R.id.medicationAtpnWarnQesitm);
                        TextView atpnWarnQesitmText = medicationFrame.findViewById(R.id.viewmedicationAtpnWarnQesitm);
                        TextView Text7 = medicationFrame.findViewById(R.id.medicationAtpnQesitm);
                        TextView atpnQesitmText = medicationFrame.findViewById(R.id.viewmedicationAtpnQesitm);
                        TextView Text8 = medicationFrame.findViewById(R.id.medicationIntrcQesitm);
                        TextView intrcQesitm = medicationFrame.findViewById(R.id.viewmedicationIntrcQesitm);
                        TextView Text9 = medicationFrame.findViewById(R.id.medicationSeQesitm);
                        TextView seQesitmText = medicationFrame.findViewById(R.id.viewmedicationSeQesitm);
                        TextView Text10 = medicationFrame.findViewById(R.id.medicationDepositMethodQesitm);
                        TextView depositMethodQesitmText = medicationFrame.findViewById(R.id.viewmedicationDepositMethodQesitm);

                        nameText.setText(medication.getItemName());
                        entpNameText.setText(medication.getEntpName());
                        itemSeqText.setText(medication.getItemSeq());
                        efcyQesitmText.setText(medication.getEfcyQesitm());
                        useMethodQesitmText.setText(medication.getUseMethodQesitm());
                        atpnWarnQesitmText.setText(medication.getAtpnWarnQesitm());
                        atpnQesitmText.setText(medication.getAtpnQesitm());
                        intrcQesitm.setText(medication.getIntrcQesitm());
                        seQesitmText.setText(medication.getSeQesitm());
                        depositMethodQesitmText.setText(medication.getDepositMethodQesitm());


                        medicationFrames.add(medicationFrame);

                        Button editButton = medicationFrame.findViewById(R.id.editButton);
                        Button deleteButton = medicationFrame.findViewById(R.id.deleteButton);

                        editButton.setOnClickListener(v -> runOnUiThread(() -> {
                            LayoutInflater dialogInflater = LayoutInflater.from(ViewDetailActivity.this);
                            View editView = dialogInflater.inflate(R.layout.medication_frame, null);

                            AutoCompleteTextView nameEdit = editView.findViewById(R.id.editmedicationName);
                            nameEdit.setAdapter(adapter);
                            nameEdit.setThreshold(1);

                            nameEdit.setText(medication.getItemName());

//                            new androidx.appcompat.app.AlertDialog.Builder(ViewDetailActivity.this)
//                                    .setTitle("의약품 정보 수정")
//                                    .setView(editView)
//                                    .setPositiveButton("수정", (dialog, which) -> {
//                                        new Thread(() -> {
//                                            String name = nameEdit.getText().toString();
//                                            String n_name = normalizeMedicineName(name);
//                                            MedicineTable toEdit_medicineTable = medicineTableRepository.getMedicationByName(n_name);
//
//                                            if (name.isEmpty()) { //의약품 이름이 비면 수정사항 적용하지 않음.
//                                                runOnUiThread(() ->
//                                                        Toast.makeText(ViewDetailActivity.this, "의약품 이름이 비어있습니다", Toast.LENGTH_SHORT).show()
//                                                );
//                                                return;
//                                            }
//                                            medication.setName(name);
//                                            medication.setDosage(CommonMethod.parseInteger(dosageEdit.getText().toString()));
//                                            medication.setFrequency(CommonMethod.parseInteger(freqencyEdit.getText().toString()));
//                                            medication.setIngredients(toEdit_medicineTable.getIngredients());
//                                            medication.setAppearance(toEdit_medicineTable.getAppearance());
//                                            medication.setEffects(toEdit_medicineTable.getEffects());
//                                            medication.setCaution(toEdit_medicineTable.getCaution());
//
//                                            medicationRepository.update(medication);
//
//                                            runOnUiThread(() -> {
//                                                nameText.setText(medication.getName());
//                                                dosageText.setText(String.valueOf(medication.getDosage()));
//                                                frequencyText.setText(String.valueOf(medication.getFrequency()));
//                                                ingredientsText.setText(medication.getIngredients());
//                                                appearanceText.setText(medication.getAppearance());
//                                                effectsText.setText(medication.getEffects());
//                                                cautionText.setText(medication.getCaution());
//                                            });
//                                        }).start();
//                                    })
//                                    .setNegativeButton("취소", null)
//                                    .show();
                            // 위 AlertDialog.Builder 방식으로 다이얼로그를 생성하면 수정 or 취소 버튼 터치 시 무조건 팝업창을 닫게 되어있음.
                            // 이를 방지하기 위해 아래와 같이 직접 AlertDialog 객체를 생성하고 수동으로 팝업창을 닫게 설정함.
                            AlertDialog dialog = new AlertDialog.Builder(ViewDetailActivity.this)
                                    .setTitle("의약품 정보 수정")
                                    .setView(editView)
                                    .setPositiveButton("수정", null) //null은 아무 것도 수행하지 말고 팝업창을 닫으라는 의미. 그러나 뒤에 별도의 리스너를 부착했기 때문에 dismiss()가 호출되기 전까지는 팝업창이 닫히지 않는다.
                                    .setNegativeButton("취소", null)
                                    .create();

                            dialog.setOnShowListener(dialogInterface -> {
                                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE); //BUTTON_POSITIVE은 Positive버튼을 의미하는 AlertDialog 클래스에 미리 정의된 상수이다. 내가 선언 한거 아님.
                                positiveButton.setOnClickListener(view -> { //수정 버튼에 별도의 리스너를 부착했기 때문에 dismiss() 호출 전까지는 팝업창이 닫히지 않는다.
                                    String name = nameEdit.getText().toString().trim();

                                    if (name.isEmpty()) {
                                        Toast.makeText(ViewDetailActivity.this, "의약품 이름이 비어있습니다", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    new Thread(() -> {

                                        Medication newData = CommonMethod.getDrugInfo(apiKey, name);

                                        medication.setItemName(newData.getItemName());
                                        medication.setEntpName(newData.getEntpName());
                                        medication.setEfcyQesitm(newData.getEfcyQesitm());
                                        medication.setUseMethodQesitm(newData.getUseMethodQesitm());
                                        medication.setAtpnWarnQesitm(newData.getAtpnWarnQesitm());
                                        medication.setAtpnQesitm(newData.getAtpnQesitm());
                                        medication.setIntrcQesitm(newData.getIntrcQesitm());
                                        medication.setSeQesitm(newData.getSeQesitm());
                                        medication.setDepositMethodQesitm(newData.getDepositMethodQesitm());

                                        medicationRepository.update(medication);

                                        runOnUiThread(() -> {
                                            nameText.setText(medication.getItemName());
                                            entpNameText.setText(medication.getEntpName());
                                            itemSeqText.setText(medication.getItemSeq());
                                            efcyQesitmText.setText(medication.getEfcyQesitm());
                                            useMethodQesitmText.setText(medication.getUseMethodQesitm());
                                            atpnWarnQesitmText.setText(medication.getAtpnWarnQesitm());
                                            atpnQesitmText.setText(medication.getAtpnQesitm());
                                            intrcQesitm.setText(medication.getIntrcQesitm());
                                            seQesitmText.setText(medication.getSeQesitm());
                                            depositMethodQesitmText.setText(medication.getDepositMethodQesitm());
                                            dialog.dismiss(); // 다이얼로그 닫기
                                        });
                                    }).start();
                                });
                            });

                            dialog.show();

                        }));

                        deleteButton.setOnClickListener(v -> runOnUiThread(() -> {
                            new androidx.appcompat.app.AlertDialog.Builder(ViewDetailActivity.this)
                                    .setTitle("의약품 정보 삭제")
                                    .setMessage("정말로 삭제하시겠습니까?")
                                    .setPositiveButton("삭제", (dialog, which) -> {
                                        new Thread(() -> {
                                            prescription_viewRepository.deleteByAllKey(prescriptionId, medication.getId());
                                            medicationRepository.delete(medication);

                                            List<Prescription_View> remaining = prescription_viewRepository.getMedicationsForPrescription(prescriptionId);

                                            if (remaining.isEmpty()) {
                                                prescriptionRepository.delete(prescription);
                                                runOnUiThread(() -> {
                                                    Toast.makeText(this, "의약품이 모두 삭제되어 처방전도 삭제하였습니다!", Toast.LENGTH_SHORT).show();
                                                    setResult(RESULT_OK, new Intent().putExtra("prescriptionDeleted", true));
                                                    finish();
                                                });
                                            } else {
                                                runOnUiThread(() -> {
                                                    setResult(RESULT_OK, new Intent().putExtra("medicationDeleted", true));
                                                    Toast.makeText(this, "의약품을 삭제하였습니다!", Toast.LENGTH_SHORT).show();
                                                    medicationContainer.removeView(medicationFrame);
                                                });
                                            }
                                        }).start();
                                    })
                                    .setNegativeButton("취소", null)
                                    .show();
                        }));

                    }
                }

                runOnUiThread(() -> {
                    for (View frame : medicationFrames) {
                        medicationContainer.addView(frame);
                    }
                });

            }).start();

            prescriptiondeleteButton.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(ViewDetailActivity.this)
                        .setTitle("처방전 삭제")
                        .setMessage("이 처방전과 포함된 의약품을 삭제합니다.")
                        .setPositiveButton("삭제", (dialog, which) -> {
                            new Thread(() -> {
                                List<Prescription_View> meds = prescription_viewRepository.getMedicationsForPrescription(prescriptionId);
                                for (Prescription_View pv : meds) {
                                    long mid = pv.getMedication_id();
                                    prescription_viewRepository.deleteByAllKey(prescriptionId, mid);
                                    Medication med = medicationRepository.getMedicationById(mid);
                                    if (med != null) medicationRepository.delete(med);
                                }

                                prescriptionRepository.deletePrescriptionById(prescriptionId);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "처방전을 삭제하였습니다!", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK, new Intent().putExtra("prescriptionDeleted", true));
                                    finish();
                                });
                            }).start();
                        })
                        .setNegativeButton("취소", null)
                        .show();
            });
        }
    }

    private String normalizeMedicineName(String name) {
        if (name == null) return "";

        // 1. 기본 정규화: 앞뒤 공백 제거
        name = name.trim();

        // 2. 괄호와 그 내용 제거
        name = name.replaceAll("\\(.*?\\)", "").trim();

        // 3. 언더스코어(_)와 그 이후 내용 제거
        name = name.replaceAll("_.*$", "").trim();

        // 4. 추가 공백 제거
        name = name.replaceAll("\\s+", " ").trim();

        // 5. 숫자 이후의 문자열 모두 제거
        name = name.replaceAll("\\d.*$", "").trim();

        // 6. 특수문자 제거 (한글, 영문만 남김)
        name = name.replaceAll("[^가-힣a-zA-Z]", "").trim();

        // 7. 마지막 언더스코어 제거
        name = name.replaceAll("_$", "").trim();

        Log.d(TAG, "정규화된 약품명: " + name);
        return name;
    }
}
