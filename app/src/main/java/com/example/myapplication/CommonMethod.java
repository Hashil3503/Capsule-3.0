package com.example.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CommonMethod { //자주 쓰일 것 같은 메서드 모아둠

    private static final String TAG = "CommonMethod";
    private static final String SERVICE_URL = "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";


    public static int parseInteger(String value) { //문자열을 int형으로 전환
        try {
            return Integer.parseInt(value.trim()); // 공백 제거 후 숫자로 변환
        } catch (NumberFormatException e) {
            return 0; // 변환할 수 없는 경우 기본값 0 반환
        }
    }

    public static float parseFloat(String value) { //문자열을 int형으로 전환
        try {
            return Float.parseFloat(value.trim()); // 공백 제거 후 숫자로 변환
        } catch (NumberFormatException e) {
            return 0; // 변환할 수 없는 경우 기본값 0 반환
        }
    }

    public static String normalizeWord(String text) {
        if (text == null) return "";

        // 1. 기본 정리: 앞뒤 공백 제거
        text = text.trim();

        // 2. 숫자+밀리그람/mg 단위 제거
        text = text.replaceAll("\\d+\\s*(밀리그람|밀리그램|mg|MG)", "").trim();

        // 3. 괄호로 시작해 괄호로 끝나는 문자열 제거 후, 괄호가 열린채로 끊긴 문자열 제거.
        text = text.replaceAll("\\([^\\)]*\\)", "").trim();
        text = text.replaceAll("[\\(].*$", "").trim();

        // 4. 제형 키워드 중 가장 먼저 등장하는 위치 찾기
        String[] forms = {"정제", "정", "캡슐", "셀", "시럽", "연고", "크림", "가루", "연고", "액", "환", "과립", "파스", "패치", "필름", "주사제", "주사", "젤"};
        int earliestIdx = text.length();
        String selectedForm = null;

        for (String form : forms) {
            int idx = text.indexOf(form);
            if (idx != -1 && idx < earliestIdx) {
                earliestIdx = idx;
                selectedForm = form;
            }
        }

        // 4. 제형 기준으로 자르기
        if (selectedForm != null) {
            int cutIdx = earliestIdx + selectedForm.length();
            text = text.substring(0, Math.min(cutIdx, text.length())).trim();
        } else {
            text = text.trim();
        }

        if (text.length() < 2) return "";

        return text;
    }

    public static Medication getDrugInfo(String apiKey, String drugName) {
        Medication medication = null;
        String nolName = normalizeWord(drugName);
        try {
            String urlStr = SERVICE_URL +
                    "?serviceKey=" + URLEncoder.encode(apiKey, "UTF-8") +
                    "&type=json" +
                    "&itemName=" + URLEncoder.encode(nolName, "UTF-8") +
                    "&numOfRows=1&pageNo=1";

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

            JSONObject json = new JSONObject(response.toString());
            JSONObject body = json.optJSONObject("body");
            if (body == null) return null;

            Object itemsNode = body.opt("items");
            JSONArray items = null;
            if (itemsNode instanceof JSONArray) {
                items = (JSONArray) itemsNode;
            } else if (itemsNode instanceof JSONObject) {
                items = ((JSONObject) itemsNode).optJSONArray("item");
            }

            if (items == null || items.length() == 0) {
                medication = new Medication();
                medication.setItemName(drugName);
                medication.setEntpName("제조사 정보 없음");
                medication.setItemSeq("품목기준코드 정보 없음");
                medication.setEfcyQesitm("효능 정보 없음");
                medication.setUseMethodQesitm("사용 정보 없음");
                medication.setAtpnWarnQesitm("경고 정보 없음");
                medication.setAtpnQesitm("주의사항 정보 없음");
                medication.setIntrcQesitm("병용금기 정보 없음");
                medication.setSeQesitm("부작용 정보 없음");
                medication.setDepositMethodQesitm("보관 정보 없음");
            };

            JSONObject item = items.getJSONObject(0); // 첫 번째 결과만 사용
            medication = new Medication();
            medication.setItemName(item.optString("itemName"));
            medication.setEntpName(item.optString("entpName", "제조사 정보 없음"));
            medication.setItemSeq(item.optString("itemSeq", "품목기준코드 정보 없음"));
            medication.setEfcyQesitm(item.optString("efcyQesitm", "효능 정보 없음"));
            medication.setUseMethodQesitm(item.optString("useMethodQesitm", "사용 정보 없음"));
            medication.setAtpnWarnQesitm(item.optString("atpnWarnQesitm", "경고 정보 없음"));
            medication.setAtpnQesitm(item.optString("atpnQesitm", "주의사항 정보 없음"));
            medication.setIntrcQesitm(item.optString("intrcQesitm", "병용금기 정보 없음"));
            medication.setSeQesitm(item.optString("seQesitm", "부작용 정보 없음"));
            medication.setDepositMethodQesitm(item.optString("depositMethodQesitm", "보관 정보 없음"));

            Log.d(TAG, "API 반환 약품: " + medication.getItemName());

        } catch (Exception e) {
            Log.e(TAG, "API 호출 실패", e);
        }

        return medication;
    }

}

