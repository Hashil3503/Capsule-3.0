package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 약 도감 테이블 (MedicineDex)
 * 처방전 등록 시 자동으로 갱신됨
 */
@Entity(tableName = "medicine_dex_table")
public class MedicineDex {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String medicineName;   // 약 이름
    private String addedDates;     // 날짜 누적 ("2025.11.03,2025.11.10" 형식)
    private String imageUri;       // 이미지 경로 (비워둬도 됨)

    public MedicineDex(String medicineName, String addedDates, String imageUri) {
        this.medicineName = medicineName;
        this.addedDates = addedDates;
        this.imageUri = imageUri;
    }

    // ✅ Getter / Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getAddedDates() { return addedDates; }
    public void setAddedDates(String addedDates) { this.addedDates = addedDates; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    // ✅ 날짜 추가 (기존 값 뒤에 이어붙임)
    public void addDate(String date) {
        if (addedDates == null || addedDates.isEmpty()) {
            addedDates = date;
        } else {
            addedDates = addedDates + "," + date;
        }
    }

    // ✅ 등록된 날짜들을 배열로 반환
    public String[] getDateList() {
        if (addedDates == null || addedDates.isEmpty()) {
            return new String[]{};
        }
        return addedDates.split(",");
    }

    // ✅ 최근 등록 날짜 반환
    public String getLatestDate() {
        String[] dates = getDateList();
        if (dates.length == 0) return "";
        return dates[dates.length - 1];
    }

    // ✅ 등록 횟수 반환
    public int getCount() {
        return getDateList().length;
    }
}
