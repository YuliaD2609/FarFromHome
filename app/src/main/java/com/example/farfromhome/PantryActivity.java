package com.example.farfromhome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
    private List<Item> items= new ArrayList<>();;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VerticalMenuFragment categoriesFragment = new VerticalMenuFragment();
        fragmentTransaction.replace(R.id.vertical_menu, categoriesFragment);

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Dispensa");
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        fragmentTransaction.commit();

        itemList = findViewById(R.id.itemList);
        addItemButton = findViewById(R.id.addItemButton);

        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        dbHelper = new DatabaseHelper(this);



        Item i=new Item("prova", 3, new Date(2025, 12, 12));
        dbHelper.addItem(i, "Cucina");


        if(categoryName!=null){
            items = dbHelper.getPantryItemsByCategory(categoryName);;
            itemAdapter = new PantryItemAdapter(this, items);
            itemList.setLayoutManager(new LinearLayoutManager(this));
            itemList.setAdapter(itemAdapter);
        }

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(PantryActivity.this, PantryAddProduct.class);
            startActivity(intent);
        });
    }
}
