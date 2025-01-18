package com.example.farfromhome.pantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.menu.HorizontalMenuFragment;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;
import com.example.farfromhome.menu.VerticalMenuFragment;

import java.util.Date;

public class PantryActivity extends AppCompatActivity {

    private LinearLayout addItemButton;
    private DatabaseHelper dbHelper;
    private PantryItemsFragment pantryItemFragment;

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

        dbHelper = new DatabaseHelper(this);

        Item i = new Item("prova", 3, new Date(2025, 12, 12), "Cucina");
        dbHelper.addPantryItem(i);

        pantryItemFragment = new PantryItemsFragment();
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            Bundle pantryBundle = new Bundle();
            pantryBundle.putString("CATEGORY_NAME", categoryName);
            pantryItemFragment.setArguments(pantryBundle);
        }
        fragmentTransaction.replace(R.id.item_fragment_container, pantryItemFragment);

        fragmentTransaction.commit();

        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(PantryActivity.this, PantryAddItem.class);
            startActivity(intent);
        });

        EditText searchInput = findViewById(R.id.search_input);
        LinearLayout searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim().toLowerCase();
            if (!query.isEmpty()) {
                boolean exists = dbHelper.doesProductPantryExist(query);
                if (exists) {
                    HomeActivity.showCustomToast(this, "L'elemento è presente nella dispensa.");
                } else {
                    HomeActivity.showCustomToast(this, "L'elemento non è presente nella dispensa.");
                }
            } else {
                HomeActivity.showCustomToast(this, "Inserisci un nome per cercare.");
            }
        });

    }

    public void updateCategory(String newCategory) {
        if (pantryItemFragment != null) {
            pantryItemFragment.updateCategory(newCategory);
        }
    }
}
