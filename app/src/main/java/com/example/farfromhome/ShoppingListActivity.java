package com.example.farfromhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingListActivity extends Activity {
    private RecyclerView itemList;
    private ShoppingItemAdapter itemAdapter;
    private List<PantryItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_layout);
        TextView titolo= findViewById(R.id.title);
        titolo.setText("Lista della spesa");


        itemList = findViewById(R.id.ShoppingItemList);
        Button addItemButton = findViewById(R.id.addItemButton);

        // Setup RecyclerView
        items = new ArrayList<>(); // Load items from database here
        PantryItem i=new PantryItem("prova", 3, 1, null);
        items.add(i);
        itemAdapter = new ShoppingItemAdapter(this, items);
        itemList.setLayoutManager(new LinearLayoutManager(this));
        itemList.setAdapter(itemAdapter);

        // Add Item Button Click Listener
        //addItemButton.setOnClickListener(v -> {
           // Intent intent = new Intent(ShoppingListActivity.this, ListAddProduct.class);
           // startActivity(intent);
        //});
    }

    // Update item quantity logic in ItemAdapter
    public void updateItemQuantity(int position, int change) {
        PantryItem item = items.get(position);
        int newQuantity = item.getQuantity() + change;
        if (newQuantity >= 0) {
            item.setQuantity(newQuantity);
            itemAdapter.notifyItemChanged(position);
        }
    }
}
