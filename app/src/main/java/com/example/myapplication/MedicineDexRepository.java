package com.example.myapplication;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.myapplication.util.RewardManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 약 도감 Repository
 * Dao와 UI(Activity) 사이를 연결하는 중간 계층
 */
public class MedicineDexRepository {

    // 추가 - 김명진
    private final Application application;
    private final MedicineDexDao medicineDexDao;
    private final ExecutorService executorService;

    public MedicineDexRepository(Application application) {
        this.application = application;

        DB db = DB.getInstance(application);
        medicineDexDao = db.medicineDexDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ✅ 전체 도감 목록 가져오기
    public void getAllMedicineDex(Callback<List<MedicineDex>> callback) {
        executorService.execute(() -> {
            List<MedicineDex> result = medicineDexDao.getAllMedicineDex();
            callback.onResult(result);
        });
    }

    // ✅ 삽입
    public void insert(MedicineDex dex) {
        executorService.execute(() -> medicineDexDao.insert(dex));
    }

    // ✅ 업데이트
    public void update(MedicineDex dex) {
        executorService.execute(() -> medicineDexDao.update(dex));
    }

    // ✅ 삭제
    public void delete(MedicineDex dex) {
        executorService.execute(() -> medicineDexDao.delete(dex));
    }

    // ✅ 자동 추가 or 날짜 갱신 (이미 존재하면 날짜 추가)
    public void insertOrUpdate(String medicineName) {
        executorService.execute(() -> {
            String today = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
            MedicineDex existing = medicineDexDao.getMedicineByName(medicineName);

            if (existing != null) {
                existing.addDate(today);
                medicineDexDao.update(existing);
            } else {
                MedicineDex newDex = new MedicineDex(medicineName, today, null);
                medicineDexDao.insert(newDex);

                // ✅ 여기서 리워드 +100 지급
                RewardManager.add(application, 100);

                // ✅ 현재 리워드 잔액
                int updated = RewardManager.getBalance(application);

                // ✅ Toast는 메인 스레드에서 띄워야 해서, 핸들러로 넘겨주기
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(
                            application,
                            "리워드 +100 ( 총 : " + updated + " )",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    // ✅ 콜백 인터페이스 (UI 스레드로 결과 전달용)
    public interface Callback<T> {
        void onResult(T result);
    }
}
