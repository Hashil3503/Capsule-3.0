package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DefaultAlarmSetDao {

    // 새로운 기본 알람 시간 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE) //기존 레코드 존재하면 덮어쓰기
    void insert(DefaultAlarmSet alarmSet);

    // 기존 기본 알람 시간 수정
    @Update
    void update(DefaultAlarmSet alarmSet);

    // 특정 ID의 기본 알람 시간 삭제
    @Delete
    void delete(DefaultAlarmSet alarmSet);

    // id로 레코드 검색
    @Query("SELECT * FROM defaultAlarmSet WHERE id = :id LIMIT 1")
    DefaultAlarmSet getById(long id);

    // 전체 기본 알람 시간 조회
    @Query("SELECT * FROM defaultAlarmSet ORDER BY id ASC")
    List<DefaultAlarmSet> getAll();

    // 기존 데이터 전체 삭제 (초기화용)
    @Query("DELETE FROM defaultAlarmSet")
    void deleteAll();
}
