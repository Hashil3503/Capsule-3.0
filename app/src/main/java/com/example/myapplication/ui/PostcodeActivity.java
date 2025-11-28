package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class PostcodeActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM_NAME = "item_name";
    private static final String EXTRA_ITEM_PRICE = "item_price";

    private EditText etZip, etAddress1, etAddress2;
    private FrameLayout webContainer;
    private WebView webView;
    private String itemName;
    private int itemPrice;

    @SuppressLint("SetJavaScriptEnabled")
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcode);

        itemName  = getIntent().getStringExtra(EXTRA_ITEM_NAME);
        itemPrice = getIntent().getIntExtra(EXTRA_ITEM_PRICE, 0);

        etZip      = findViewById(R.id.etZip);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        Button btnSearch  = findViewById(R.id.btnSearch);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        webContainer = findViewById(R.id.webContainer);
        webView      = findViewById(R.id.webView);

        // WebView 설정
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        // JS → Android 브릿지
        webView.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void onAddressSelected(final String zonecode, final String addr){
                runOnUiThread(() -> {
                    etZip.setText(zonecode);
                    etAddress1.setText(addr);
                    // 주소를 받았으니 WebView 오버레이를 닫음
                    webContainer.setVisibility(View.GONE);
                });
            }
        }, "Android");

        // 주소검색 버튼: 오버레이 보여주고 우편번호 페이지 로드
        btnSearch.setOnClickListener(v -> {
            webContainer.setVisibility(View.VISIBLE);
            loadPostcodeHtml(); // 아래 메서드 참고
        });

        // 입력완료(구매 진행): 주소 검증 후 결과 반환
        btnConfirm.setOnClickListener(v -> {
            String zip  = etZip.getText().toString().trim();
            String a1   = etAddress1.getText().toString().trim();
            String a2   = etAddress2.getText().toString().trim();

            if (zip.isEmpty() || a1.isEmpty()){
                Toast.makeText(this, "주소검색으로 기본주소를 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String full = "[" + zip + "] " + a1 + (a2.isEmpty() ? "" : " / " + a2);

            Intent data = new Intent();
            data.putExtra("address", full);
            data.putExtra(EXTRA_ITEM_NAME, itemName);
            data.putExtra(EXTRA_ITEM_PRICE, itemPrice);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    private void loadPostcodeHtml(){
        // ① assets 파일을 쓰고 싶다면:
        // webView.loadUrl("file:///android_asset/postcode_embed.html");

        // ② 문자열로 직접 로드(안정적)
        String html =
                "<!DOCTYPE html><html><head><meta charset='utf-8'>"
                        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                        + "<title>주소 검색</title>"
                        + "<style>html,body,#wrap{height:100%;margin:0} #wrap{position:relative;background:#fff}</style>"
                        + "<script src='https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'></script>"
                        + "</head><body><div id='wrap'></div>"
                        + "<script>"
                        + " new daum.Postcode({"
                        + "   oncomplete:function(data){"
                        + "     var addr = data.roadAddress || data.jibunAddress || '';"
                        + "     if(window.Android && window.Android.onAddressSelected){"
                        + "       Android.onAddressSelected(data.zonecode, addr);"
                        + "     }"
                        + "   }"
                        + " }).embed(document.getElementById('wrap'));"
                        + "</script></body></html>";

        webView.loadDataWithBaseURL(
                "https://t1.daumcdn.net",
                html, "text/html", "utf-8", null);
    }

    @Override protected void onDestroy() {
        // WebView 메모리 누수 방지
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
