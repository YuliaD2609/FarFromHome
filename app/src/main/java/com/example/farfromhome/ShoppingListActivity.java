package com.example.farfromhome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {
    private RecyclerView itemList;
    private ShoppingItemAdapter itemAdapter;
    private List<PantryItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VerticalMenuFragment categoriesFragment = new VerticalMenuFragment();
        fragmentTransaction.replace(R.id.vertical_menu, categoriesFragment);

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Lista della spesa");
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        fragmentTransaction.commit();

        itemList = findViewById(R.id.ShoppingItemList);
        Button addItemButton = findViewById(R.id.addItemButton);

        items = new ArrayList<>();
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

    public void updateItemQuantity(int position, int change) {
        PantryItem item = items.get(position);
        int newQuantity = item.getQuantity() + change;
        if (newQuantity >= 0) {
            item.setQuantity(newQuantity);
            itemAdapter.notifyItemChanged(position);
        }
    }
}
