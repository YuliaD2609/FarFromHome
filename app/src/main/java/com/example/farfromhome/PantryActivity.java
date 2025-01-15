package com.example.farfromhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private RecyclerView itemList;
    private Button addItemButton;
    private PantryItemAdapter itemAdapter;
    private List<PantryItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_layout);

        TextView titolo= findViewById(R.id.title);
        titolo.setText("Dispensa");

        VerticalMenuFragment categoriesFragment = new VerticalMenuFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.vertical_menu, categoriesFragment);
        fragmentTransaction.commit();

        itemList = findViewById(R.id.itemList);
        addItemButton = findViewById(R.id.addItemButton);

        // Setup RecyclerView
        items = new ArrayList<>(); // Load items from database here
        PantryItem i=new PantryItem("prova", 3, null, new Date(2025, 12, 12));
        items.add(i);
        itemAdapter = new PantryItemAdapter(this, items);
        itemList.setLayoutManager(new LinearLayoutManager(this));
        itemList.setAdapter(itemAdapter);

        // Add Item Button Click Listener
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(PantryActivity.this, PantryAddProduct.class);
            startActivity(intent);
        });
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
