package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_dex);

        recyclerView = findViewById(R.id.recyclerView);
        loadingBar  = findViewById(R.id.loadingBar);

        // ✅ 어댑터 생성 (빈 리스트로 시작)
        adapter = new MedicineDexAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ✅ 아이템 클릭 시 상세 화면으로 이동
        adapter.setOnItemClickListener(selectedItem -> {
            Intent intent = new Intent(MedicineDexActivity.this, MedicineDexDetailActivity.class);
            intent.putExtra("medicineName", selectedItem.getMedicineName());
            startActivity(intent);
        });

        repository = new MedicineDexRepository(getApplication());

        // ✅ [테스트용] 도감에 강제로 새 약 하나 추가 + 리워드 지급 테스트
//        String testName = "리워드테스트_" + System.currentTimeMillis();
//        repository.insertOrUpdate(testName);

        loadMedicineDexList();
    }

    private void loadMedicineDexList() {
        loadingBar.setVisibility(View.VISIBLE);
        repository.getAllMedicineDex(result -> runOnUiThread(() -> {
            adapter.setItems(result);
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
