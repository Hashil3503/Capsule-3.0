package com.example.myapplication;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultAlarmSetRepository {

    private final DefaultAlarmSetDao defaultAlarmSetDao;
    private final ExecutorService executorService;

    public DefaultAlarmSetRepository(Application application) {
        DB db = Room.databaseBuilder(application, DB.class, "alarm_db")
                .fallbackToDestructiveMigration()
                .build();
        defaultAlarmSetDao = db.defaultAlarmSetDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // 기본 알람 시간 저장
    public void insert(DefaultAlarmSet alarmSet) {
        executorService.execute(() -> defaultAlarmSetDao.insert(alarmSet));
    }

    // 기본 알람 시간 수정
    public void update(DefaultAlarmSet alarmSet) {
        executorService.execute(() -> defaultAlarmSetDao.update(alarmSet));
    }

    // 기본 알람 시간 삭제
    public void delete(DefaultAlarmSet alarmSet) {
        executorService.execute(() -> defaultAlarmSetDao.delete(alarmSet));
    }

    // 모든 기본 알람 시간 가져오기 (List 반환)
    public List<DefaultAlarmSet> getAll() {
        return defaultAlarmSetDao.getAll();
    }

    // id로 레코드 검색
    public DefaultAlarmSet getById(long id) {
        return defaultAlarmSetDao.getById(id);
    }
    
    // 전체 삭제 (초기화용)
    public void deleteAll() {
        executorService.execute(defaultAlarmSetDao::deleteAll);
    }
}
