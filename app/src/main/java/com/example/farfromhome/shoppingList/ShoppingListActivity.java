package com.example.farfromhome.shoppingList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.menu.HorizontalMenuFragment;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;
import com.example.farfromhome.menu.VerticalMenuFragment;

import java.util.Date;

public class ShoppingListActivity extends AppCompatActivity {
    private LinearLayout addItemButton;;
    DatabaseHelper dbHelper;
    private ShoppingItemsFragment shoppingItemFragment;

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

        dbHelper = new DatabaseHelper(this);

        Item i=new Item("prova", 3, new Date(2025, 12, 12), "Cucina");
        dbHelper.addShoppingListItem(i);

        shoppingItemFragment = new ShoppingItemsFragment();
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            Bundle shoppingBundle = new Bundle();
            shoppingBundle.putString("CATEGORY_NAME", categoryName);
            shoppingItemFragment.setArguments(shoppingBundle);
        }
        fragmentTransaction.replace(R.id.ShoppingItemList, shoppingItemFragment);

        fragmentTransaction.commit();

        addItemButton = findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingListActivity.this, ShoppingAddItem.class);
            startActivity(intent);
        });

        LinearLayout suitcaseDoneButton = findViewById(R.id.shoppingDone);
        suitcaseDoneButton.setOnClickListener(v -> {
            shoppingItemFragment.removeMarkedItems();
        });
    }

    public void updateCategory(String newCategory) {
        shoppingItemFragment.updateCategory(newCategory);
    }
}
