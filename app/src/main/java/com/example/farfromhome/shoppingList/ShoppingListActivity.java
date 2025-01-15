package com.example.farfromhome.shoppingList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HorizontalMenuFragment;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;
import com.example.farfromhome.VerticalMenuFragment;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {
    private RecyclerView itemList;
    private ShoppingItemAdapter itemAdapter;
    private List<Item> items= new ArrayList<>();;
    DatabaseHelper dbHelper;

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

        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        dbHelper = new DatabaseHelper(this);


        if(categoryName!=null){
            items = dbHelper.getPantryItemsByCategory(categoryName);;
            itemAdapter = new ShoppingItemAdapter(this, items);
            itemList.setLayoutManager(new LinearLayoutManager(this));
            itemList.setAdapter(itemAdapter);
        }

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingListActivity.this, ShoppingAddItem.class);
            startActivity(intent);
        });
    }

    public void updateCategory(String newCategory) {
        ShoppingItemsFragment fragment = (ShoppingItemsFragment) getSupportFragmentManager().findFragmentById(R.id.ShoppingItemList);
        if (fragment != null) {
            fragment.updateCategory(newCategory);
        }
    }
}
