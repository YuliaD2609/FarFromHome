package com.example.farfromhome.shoppingList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.menu.HorizontalMenuFragment;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;
import com.example.farfromhome.menu.VerticalMenuFragment;
import com.example.farfromhome.pantry.PantryActivity;
import com.example.farfromhome.pantry.PantryAddItem;
import com.example.farfromhome.pantry.PantryItemsFragment;

import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {
    private LinearLayout addItemButton;;
    DatabaseHelper dbHelper;
    private ShoppingItemsFragment shoppingItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_layout);

        dbHelper = new DatabaseHelper(this);
        setupFragments();
        setupUI();
    }

    private void setupFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.vertical_menu, new VerticalMenuFragment());

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Lista della spesa");
        bundle.putBoolean("SHOW_CART", true);
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        shoppingItemFragment = new ShoppingItemsFragment();
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            Bundle shoppingBundle = new Bundle();
            shoppingBundle.putString("CATEGORY_NAME", categoryName);
            shoppingItemFragment.setArguments(shoppingBundle);
        }
        fragmentTransaction.replace(R.id.ShoppingItemList, shoppingItemFragment);

        fragmentTransaction.commit();
    }

    private void setupUI() {
        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingListActivity.this, ShoppingAddItem.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        EditText searchInput = findViewById(R.id.search_input);
        LinearLayout searchButton = findViewById(R.id.search_button);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(searchInput);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchButton.setOnClickListener(v -> {
            search(searchInput);
        });
    }

    public void updateCategory(String newCategory) {
        shoppingItemFragment.updateCategory(newCategory);
    }

    public void search(EditText searchInput){
        String query = searchInput.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            HomeActivity.showCustomToast(this, "Inserisci un nome per cercare.");
            shoppingItemFragment.loadItems(VerticalMenuFragment.getSelectedCategory());
            return;
        }

        String selectedCategory = VerticalMenuFragment.getSelectedCategory();
        List<Item> searchResults = (selectedCategory == null || selectedCategory.isEmpty())
                ? dbHelper.searchShoppingListItemsByName(query)
                : dbHelper.searchShoppingListItemsByCategoryAndName(selectedCategory, query);

        if (searchResults.isEmpty()) {
            HomeActivity.showCustomToast(this, "Nessun elemento trovato con questo nome.");
        } else {
            shoppingItemFragment.updateItemList(searchResults);
        }
    }
}
