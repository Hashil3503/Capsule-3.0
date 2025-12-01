package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

/**
 * 약 도감 상세 화면
 * - 도감에서 약을 클릭하면 이 액티비티로 넘어와서
 * - 효능/효과, 부작용, 주의사항 등을 보여준다.
 * - 데이터 출처: Medication 테이블 (e약은요 API 결과 저장된 곳)
 */
public class MedicineDexDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEffect;
    private TextView tvSideEffect;
    private TextView tvCaution;
    private ImageView ivImage;

    private String apiKey; //e약은요 apiKey

    private MedicationRepository medicationDexRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_dex_detail);

        // 뷰 찾기
        tvName = findViewById(R.id.tvName);
        tvEffect = findViewById(R.id.tvEffect);
        tvSideEffect = findViewById(R.id.tvSideEffect);
        tvCaution = findViewById(R.id.tvCaution);
        ivImage = findViewById(R.id.ivImage);

        apiKey = getString(R.string.med_search_api_key); //e약은요 api 키 가져오기

        // Repo 생성
        medicationDexRepository = new MedicationRepository(getApplication());

        // 도감에서 넘겨준 약 이름 받기 (MedicineDexActivity에서 넣어준 키: "medicineName")
        String medicineName = getIntent().getStringExtra("medicineName");

        if (medicineName == null || medicineName.isEmpty()) {
            tvName.setText("약 이름 정보를 가져올 수 없습니다.");
            tvEffect.setText("");
            tvSideEffect.setText("");
            tvCaution.setText("");
            return;
        }

        // 제목에 약 이름 먼저 표시
        tvName.setText(medicineName);

        // Medication 테이블에서 약 정보 조회 (이름으로 검색)
        new Thread(() -> {
            // 🔹 잠시 후에 만들 메서드: medicationRepository.getMedicationByName(medicineName);
            Medication medication = medicationDexRepository.getMedicationByName(medicineName);
            Medication medication_api = CommonMethod.getDrugInfo(apiKey, medicineName);
            String url = CommonMethod.getDrugImageUrl(apiKey, medicineName);

            runOnUiThread(() -> {
                if (medication != null) {
                    // 이름
                    tvName.setText(medication.getItemName());

                    // 효능/효과
                    String effectText = medication.getEfcyQesitm();
                    tvEffect.setText(
                            effectText != null && !effectText.isEmpty()
                                    ? effectText
                                    : "등록된 효능/효과 정보가 없습니다."
                    );

                    // 부작용
                    String sideEffectText = medication.getSeQesitm();
                    tvSideEffect.setText(
                            sideEffectText != null && !sideEffectText.isEmpty()
                                    ? sideEffectText
                                    : "등록된 부작용 정보가 없습니다."
                    );

                    // 주의사항 (경고 + 주의사항 합쳐서 써도 됨)
                    String cautionText = "";
                    if (medication.getAtpnWarnQesitm() != null) {
                        cautionText += medication.getAtpnWarnQesitm() + "\n\n";
                    }
                    if (medication.getAtpnQesitm() != null) {
                        cautionText += medication.getAtpnQesitm();
                    }
                    tvCaution.setText(
                            cautionText != null && !cautionText.trim().isEmpty()
                                    ? cautionText.trim()
                                    : "등록된 주의사항 정보가 없습니다."
                    );

                    ivImage.setImageResource(R.drawable.ic_pill);

                } else {
                    tvEffect.setText(medication_api.getEfcyQesitm());
                    tvSideEffect.setText(medication_api.getSeQesitm());
                    String cautionText = "";
                    cautionText += medication_api.getAtpnWarnQesitm() + "\n" + medication_api.getAtpnQesitm();
                    tvCaution.setText(cautionText);
                }
                if (url != null && !url.isEmpty()) {
                    Glide.with(MedicineDexDetailActivity.this)
                            .load(url)
                            .placeholder(R.drawable.ic_pill)
                            .into(ivImage);
                }
            });
        }).start();
    }
}
