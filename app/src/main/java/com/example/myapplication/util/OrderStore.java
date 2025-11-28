package com.example.myapplication.util;

import android.content.Context;

import com.example.myapplication.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderStore {
    private static final String PREF="orders_pref", KEY="orders";

    public static ArrayList<Order> list(Context ctx){
        ArrayList<Order> out=new ArrayList<>();
        String json = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY,"[]");
        try{
            JSONArray arr=new JSONArray(json);
            for(int i=0;i<arr.length();i++){
                JSONObject o=arr.getJSONObject(i);
                out.add(new Order(o.optString("id",""), o.optString("itemName",""),
                        o.optInt("price",0), o.optString("address","")));
            }
        }catch(JSONException ignore){}
        return out;
    }
    private static void saveAll(Context ctx, ArrayList<Order> list){
        JSONArray arr=new JSONArray();
        for (Order o: list){
            JSONObject jo=new JSONObject();
            try { jo.put("id",o.id); jo.put("itemName",o.itemName); jo.put("price",o.price); jo.put("address",o.address);}catch(JSONException ignore){}
            arr.put(jo);
        }
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply();
    }
    public static void add(Context ctx, Order order){ ArrayList<Order> cur=list(ctx); cur.add(order); saveAll(ctx,cur); }
    public static void remove(Context ctx, String id){ ArrayList<Order> cur=list(ctx); for(int i=0;i<cur.size();i++){ if(cur.get(i).id.equals(id)){cur.remove(i); break;}} saveAll(ctx,cur); }
}
