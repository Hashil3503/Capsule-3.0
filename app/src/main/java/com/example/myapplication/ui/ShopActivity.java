package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.example.myapplication.util.OrderStore;
import com.example.myapplication.util.RewardManager;

public class ShopActivity extends AppCompatActivity {
    private static final String EXTRA_ITEM_NAME = "item_name";
    private static final String EXTRA_ITEM_PRICE = "item_price";

    private TextView tvBalance;

    private final ActivityResultLauncher<Intent> addressPicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData()!=null){
                    String address  = result.getData().getStringExtra("address");
                    String itemName = result.getData().getStringExtra(EXTRA_ITEM_NAME);
                    int price       = result.getData().getIntExtra(EXTRA_ITEM_PRICE, 0);

                    new android.app.AlertDialog.Builder(this)
                            .setTitle("구매 확인")
                            .setMessage(itemName + " (" + price + " 리워드)\n" + address + "\n이대로 구매할까요?")
                            .setPositiveButton("구매", (d,w)->{
                                boolean ok = RewardManager.spend(this, price);
                                if (!ok){ Toast.makeText(this, "리워드가 부족합니다.", Toast.LENGTH_SHORT).show(); refreshBalance(); return; }
                                String id = String.valueOf(System.currentTimeMillis());
                                OrderStore.add(this, new Order(id, itemName, price, address));
                                Toast.makeText(this, "구매 완료!", Toast.LENGTH_SHORT).show();
                                refreshBalance();
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
            });

    private void refreshBalance(){ tvBalance.setText("리워드: " + RewardManager.getBalance(this)); }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        tvBalance = findViewById(R.id.tvShopBalance);
        Button btnVitamin = findViewById(R.id.btnVitamin);
        Button btnMask    = findViewById(R.id.btnMask);
        Button btnOrders  = findViewById(R.id.btnOrders);

        refreshBalance();
        btnVitamin.setOnClickListener(v -> tryBuy("비타민C", 100));
        btnMask.setOnClickListener(v -> tryBuy("마스크", 200));
        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, OrdersActivity.class)));
    }

    @Override protected void onResume() { super.onResume(); refreshBalance(); }

    private void tryBuy(String itemName, int price){
        if (RewardManager.getBalance(this) < price){ Toast.makeText(this, "리워드가 부족합니다.", Toast.LENGTH_SHORT).show(); return; }
        Intent i = new Intent(this, PostcodeActivity.class);
        i.putExtra(EXTRA_ITEM_NAME, itemName);
        i.putExtra(EXTRA_ITEM_PRICE, price);
        addressPicker.launch(i);
    }
}
