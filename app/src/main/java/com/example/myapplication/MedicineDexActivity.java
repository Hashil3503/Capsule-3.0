package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * 약 도감 화면 (MedicineDexActivity)
 * RecyclerView로 약 이름 + 등록 날짜 표시
 */
public class MedicineDexActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineDexAdapter adapter;
    private MedicineDexRepository repository;
    private ProgressBar loadingBar;

    private ImageView ivThumb;

    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_dex);

        recyclerView = findViewById(R.id.recyclerView);
        loadingBar  = findViewById(R.id.loadingBar);

        apiKey = getString(R.string.med_search_api_key); //e약은요 api 키 가져오기


        // ✅ 어댑터 생성 (빈 리스트로 시작)
        adapter = new MedicineDexAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ✅ 아이템 클릭 시 상세 화면으로 이동
        adapter.setOnItemClickListener(selectedItem -> {
            Intent intent = new Intent(MedicineDexActivity.this, MedicineDexDetailActivity.class);
            intent.putExtra("medicineName", selectedItem.getMedicineName());
            startActivity(intent);
        });

        repository = new MedicineDexRepository(getApplication());

        loadMedicineDexList();
    }

    private void loadMedicineDexList() {
        loadingBar.setVisibility(View.VISIBLE);
        repository.getAllMedicineDex(result -> runOnUiThread(() -> {
            adapter.setItems(result);

            // 이미지 URL을 비동기로 하나씩 받아와서 adapter에 저장
            for (MedicineDex item : result) {
                new Thread(() -> {
                    String url = CommonMethod.getDrugImageUrl(apiKey, item.getMedicineName());

                    // UI thread에서 캐시에 저장
                    runOnUiThread(() -> {
                        adapter.putImageUrl(item.getMedicineName(), url);
                        adapter.notifyItemChanged(result.indexOf(item)); // 이미지 갱신
                    });
                }).start();
            }

            loadingBar.setVisibility(View.GONE);


            // ✅ 몇 개 로드됐는지 로그 + 토스트로 확인
            android.util.Log.d("MedicineDex", "도감 개수 = " + result.size());

            if (result.isEmpty()) {
                android.widget.Toast.makeText(
                        MedicineDexActivity.this,
                        "도감에 등록된 약이 아직 없습니다.",
                        android.widget.Toast.LENGTH_SHORT
                ).show();
            }

            adapter.setItems(result);


        }));
    }
}
