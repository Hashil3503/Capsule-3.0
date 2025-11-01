package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MedSearchActivity extends AppCompatActivity {

    private static final String TAG = "DrugInfoAPI";
    private static final String SERVICE_URL ="https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";

    private String apiKey;

    //ui 요소 찾기
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_med_search);

        apiKey = getString(R.string.med_search_api_key);


        //ui 요소 찾기
        tvResult = findViewById(R.id.tvResult);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //약품명 입력
        String med = "타이레놀";
        //

        //api 호출
        getDrugInfo(med);

    }
    private void getDrugInfo(String drugName) {
        new Thread(() -> {
            try {
                String urlStr = SERVICE_URL +
                        "?serviceKey=" + URLEncoder.encode(apiKey, "UTF-8") +
                        "&type=json" +
                        "&itemName=" + URLEncoder.encode(drugName, "UTF-8") +
                        "&numOfRows=10&pageNo=1";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                // JSON 파싱 (간단한 예시)
                JSONObject json = new JSONObject(response.toString());
                JSONArray items = json.getJSONObject("body").getJSONArray("items");

                StringBuilder display = new StringBuilder();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String itemName = item.getString("itemName");
                    String efcyQesitm = item.optString("efcyQesitm", "효능 정보 없음");
                    String useMethodQesitm = item.optString("useMethodQesitm", "사용 정보 없음");
                    String atpnWarnQesitm = item.optString("atpnWarnQesitm", "경고 정보 없음");
                    String atpnQesitm = item.optString("atpnQesitm", "주의사항 정보 없음");
                    String intrcQesitm = item.optString("intrcQesitm", "병용금기 정보 없음");
                    String seQesitm = item.optString("seQesitm", "부작용 정보 없음");
                    String depositMethodQesitm = item.optString("depositMethodQesitm", "보관 정보 없음");

                    String entpName = item.optString("entpName", "업체 정보 없음");
                    String itemSeq = item.optString("itemSeq", "품목기준코드 정보 없음");

                    display.append("약품명: ").append(itemName).append("\n");
//                            .append("효능: ").append(efcyQesitm).append("\n")
//                            .append("사용 방법: ").append(useMethodQesitm).append("\n")
//                            .append("복용 전 경고: ").append(atpnWarnQesitm).append("\n")
//                            .append("주의사항: ").append(atpnQesitm).append("\n")
//                            .append("병용금기 정보: ").append(intrcQesitm).append("\n")
//                            .append("발생 가능한 부작용: ").append(seQesitm).append("\n")
//                            .append("보관 방법: ").append(depositMethodQesitm).append("\n")
//                            .append("제조 업체: ").append(entpName).append("\n")
//                            .append("품목기준코드: ").append(itemSeq).append("\n");


                    Log.d("DrugInfo", "약품명: " + itemName);
                }
                if (items.length() == 0) {
                    display.append("검색 결과가 없습니다.");
                }

                runOnUiThread(() -> tvResult.setText(display.toString()));

            } catch (Exception e) {
                Log.e(TAG, "API 호출 실패", e);
                runOnUiThread(() -> tvResult.setText("오류: " + e.getMessage()));
            }
        }).start();
    }
}
