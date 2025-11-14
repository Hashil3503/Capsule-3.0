package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout setAlarmButton, setPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setAlarmButton = findViewById(R.id.setAlarmButton);
        setPasswordButton = findViewById(R.id.setPasswordButton);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetDefaultAlarmActivity.class);
                startActivity(intent);
            }
        });

        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });



    }

    private void showPasswordDialog() {
        // dialog 생성
        Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_password_window);
        dialog.setCancelable(false);  // 뒤로가기/바깥터치로 닫히지 않게 설정

        // 레이아웃 요소 가져오기
        EditText inputPassword = dialog.findViewById(R.id.inputPassword);
        TextView textPassword = dialog.findViewById(R.id.textPassword);
        TextView textWarning = dialog.findViewById(R.id.textWarning);
        Button loginBtn = dialog.findViewById(R.id.loginButton);
        Button cancelBtn = dialog.findViewById(R.id.cancelButton);

        PasswordSecurityHelper helper = new PasswordSecurityHelper(this);

        textWarning.setVisibility(View.INVISIBLE);

        textPassword.setText("기존 비밀번호 확인");

        loginBtn.setOnClickListener(v -> {
            String pw = inputPassword.getText().toString();

            if (helper.checkPassword(pw)) {
                dialog.dismiss();
                editPasswordDialog();
            } else {
                textWarning.setText("비밀번호가 틀렸습니다.");
                textWarning.setVisibility(View.VISIBLE);
            }
        });

        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // 팝업 크기 설정 (가로는 화면의 90%)
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
        }

        dialog.show();
    }

    private void editPasswordDialog() { //비밀번호 변경 전 기존 비밀번호 확인
        // dialog 생성
        Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_password_window);
        dialog.setCancelable(false);  // 뒤로가기/바깥터치로 닫히지 않게 설정

        // 레이아웃 요소 가져오기
        EditText inputPassword = dialog.findViewById(R.id.inputPassword);
        TextView textPassword = dialog.findViewById(R.id.textPassword);
        TextView textWarning = dialog.findViewById(R.id.textWarning);
        Button confirmBtn = dialog.findViewById(R.id.loginButton);
        Button cancelBtn2 = dialog.findViewById(R.id.cancelButton);

        textPassword.setText("변경할 비밀번호");

        textWarning.setVisibility(View.INVISIBLE);

        PasswordSecurityHelper helper = new PasswordSecurityHelper(this);


        confirmBtn.setOnClickListener(v -> {
            String newPw  = inputPassword.getText().toString();

            // 최소 길이 체크 (신규 PW 설정 또는 변경 시)
            if (newPw.length() < 8) {
                textWarning.setText("비밀번호는 최소 8자리 이상이어야 합니다.");
                textWarning.setVisibility(View.VISIBLE);
                return;  // 여기서 종료 → PW 저장하지 않음
            }

            if (helper.checkPassword(newPw)) {
                textWarning.setText("기존과 동일한 비밀번호 입니다.");
                textWarning.setVisibility(View.VISIBLE);
                return;  // 여기서 종료 → PW 저장하지 않음
            } else {
                changePassword(newPw);
                Toast.makeText(this, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelBtn2.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // 팝업 크기 설정 (가로는 화면의 90%)
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
        }

        dialog.show();

    }

    private void changePassword(String newPw) {
        PasswordSecurityHelper helper = new PasswordSecurityHelper(this);
        String newHash = PasswordSecurityHelper.sha256(newPw);
        helper.savePasswordHash(newHash);
    }

}