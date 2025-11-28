package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 약 도감 DAO (Data Access Object)
 * DB 접근을 위한 인터페이스
 */
@Dao
public interface MedicineDexDao {

    // ✅ 전체 도감 목록 불러오기
    @Query("SELECT * FROM medicine_dex_table ORDER BY medicineName ASC")
    List<MedicineDex> getAllMedicineDex();

    // ✅ 특정 약 이름으로 검색
    @Query("SELECT * FROM medicine_dex_table WHERE medicineName = :name LIMIT 1")
    MedicineDex getMedicineByName(String name);

    // ✅ 삽입 (기본 Room insert)
    @Insert
    void insert(MedicineDex medicineDex);

    // ✅ 업데이트
    @Update
    void update(MedicineDex medicineDex);

    // ✅ 삭제
    @Delete
    void delete(MedicineDex medicineDex);

    // ✅ 전체 삭제 (테스트용)
    @Query("DELETE FROM medicine_dex_table")
    void deleteAll();
}
