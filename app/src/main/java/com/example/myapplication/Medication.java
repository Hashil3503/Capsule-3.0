package com.example.myapplication;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//엔티티 선언 파일 : 의약품 엔티티
@Entity(tableName = "medication")
public class Medication {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String itemName;

    private String entpName; //제조 업체

    private String itemSeq; // 품목기준코드

    private String useMethodQesitm; //사용 방법

    private String atpnWarnQesitm; // 사용 전 경고

    private String atpnQesitm; // 주의사항

    private String intrcQesitm; // 상호작용

    private String depositMethodQesitm; // 보관 방법

    private String efcyQesitm; // 효능
    private String seQesitm; // 부작용

    public Medication(){} //기본 생성자
    @Ignore
    public Medication(String itemName) {
        this.itemName = itemName;
    } //생성자

    // Getter & Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getEntpName() { return entpName; }
    public void setEntpName(String entpName) { this.entpName = entpName; }

    public String getItemSeq() { return itemSeq; }
    public void setItemSeq(String itemSeq) { this.itemSeq = itemSeq; }


    public String getUseMethodQesitm() { return useMethodQesitm; }
    public void setUseMethodQesitm(String useMethodQesitm) { this.useMethodQesitm = useMethodQesitm; }

    public String getAtpnWarnQesitm() { return atpnWarnQesitm; }
    public void setAtpnWarnQesitm(String atpnWarnQesitm) { this.atpnWarnQesitm = atpnWarnQesitm; }

    public String getAtpnQesitm() { return atpnQesitm; }
    public void setAtpnQesitm(String atpnQesitm) { this.atpnQesitm = atpnQesitm; }

    public String getIntrcQesitm() { return intrcQesitm; }
    public void setIntrcQesitm(String intrcQesitm) { this.intrcQesitm = intrcQesitm; }

    public String getDepositMethodQesitm() { return depositMethodQesitm; }
    public void setDepositMethodQesitm(String depositMethodQesitm) { this.depositMethodQesitm = depositMethodQesitm; }

    public String getEfcyQesitm() { return efcyQesitm; }
    public void setEfcyQesitm(String efcyQesitm) { this.efcyQesitm = efcyQesitm; }

    public String getSeQesitm() { return seQesitm; }
    public void setSeQesitm(String seQesitm) { this.seQesitm = seQesitm; }
}