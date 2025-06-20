package com.example.product_bottomnav.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.product_bottomnav.ui.product.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderHelper {
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private String orderKey; // Dinamis sesuai email

    public OrderHelper(Context context, String email) {
        sharedPreferences = context.getSharedPreferences("order_prefs", Context.MODE_PRIVATE);
        orderKey = "order_items_" + email; // key unik per user/email
    }

    public void addToOrder(Product product) {
        List<OrderItem> orderItems = getOrderItems();
        boolean exists = false;

        for (OrderItem item : orderItems) {
            if (item.getKode().equals(product.getKode())) {
                item.setQuantity(item.getQuantity() + 1);
                exists = true;
                break;
            }
        }

        if (!exists) {
            orderItems.add(new OrderItem(product));
        }

        saveOrderItems(orderItems);
    }

    public void removeFromOrder(String productCode) {
        List<OrderItem> orderItems = getOrderItems();
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getKode().equals(productCode)) {
                orderItems.remove(i);
                break;
            }
        }
        saveOrderItems(orderItems);
    }

    public void updateQuantity(String productCode, int quantity) {
        List<OrderItem> orderItems = getOrderItems();
        for (OrderItem item : orderItems) {
            if (item.getKode().equals(productCode)) {
                item.setQuantity(quantity);
                break;
            }
        }
        saveOrderItems(orderItems);
    }

    public List<OrderItem> getOrderItems() {
        String json = sharedPreferences.getString(orderKey, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<OrderItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : getOrderItems()) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void saveOrderItems(List<OrderItem> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(orderKey, json).apply();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem item : getOrderItems()) {
            total += item.getQuantity();
        }
        return total;
    }
    public void clearOrderItems() {
        sharedPreferences.edit().remove(orderKey).apply();
    }

}
