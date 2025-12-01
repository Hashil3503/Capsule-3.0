package com.example.myapplication.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.example.myapplication.util.OrderStore;
import com.example.myapplication.util.RewardManager;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    TextView tvBal; ListView lv;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        tvBal = findViewById(R.id.tvOrdersBalance);
        lv    = findViewById(R.id.lvOrders);
        refresh();
    }

    private void refresh(){
        tvBal.setText("리워드: " + RewardManager.getBalance(this));
        ArrayList<Order> list = OrderStore.list(this);

        ArrayAdapter<Order> ad = new ArrayAdapter<Order>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, list){
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View v = super.getView(position, convertView, parent);
                TextView t1 = v.findViewById(android.R.id.text1);
                TextView t2 = v.findViewById(android.R.id.text2);
                Order o = getItem(position);
                t1.setText(o.itemName + " - " + o.price + " 리워드");
                t2.setText(o.address + " (ID:" + o.id + ")");
                return v;
            }
        };
        lv.setAdapter(ad);

        if (list.isEmpty()) {
            Toast.makeText(this, "주문이 없습니다. 상점에서 아이템을 구매해보세요.", Toast.LENGTH_SHORT).show();
        }

        lv.setOnItemClickListener((p, view, pos, id) -> {
            Order o = list.get(pos);
            new android.app.AlertDialog.Builder(this)
                    .setTitle("주문 취소")
                    .setMessage("이 주문을 취소하고 리워드 환불할까요?")
                    .setPositiveButton("예", (d,w)->{
                        OrderStore.remove(this, o.id);
                        RewardManager.refund(this, o.price);
                        Toast.makeText(this, "취소 및 환불 완료", Toast.LENGTH_SHORT).show();
                        refresh();
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        });
    }
}
