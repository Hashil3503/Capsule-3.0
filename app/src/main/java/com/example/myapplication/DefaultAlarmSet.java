package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//엔티티 선언 파일 : 기본 알람 시간대
@Entity(tableName = "defaultAlarmSet")
public class DefaultAlarmSet { //시간대를  시,분으로 저장해놨다가 나중에 Calender 객체 생성해서 값 넣어줄 예정.
    @PrimaryKey
    private long id;
    private int hour;
    private int min;

    public DefaultAlarmSet(long id, int hour, int min){
        this.id = id;
        this.hour = hour;
        this.min = min;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getHour() { return this.hour; }
    public void setHour(int hour){ this.hour = hour; }

    public int getMin() { return this.min; }
    public void setMin(int min){ this.min = min; }
}
