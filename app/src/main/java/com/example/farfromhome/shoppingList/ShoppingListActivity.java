package com.example.farfromhome.shoppingList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.List;

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
        bundle.putBoolean("SHOW_CART", true); ;
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        dbHelper = new DatabaseHelper(this);

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
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        LinearLayout shoppingDoneButton = findViewById(R.id.shoppingDone);
        shoppingDoneButton.setOnClickListener(v -> {
            shoppingItemFragment.removeMarkedItems();
            HomeActivity.showCustomToast(this, "La spesa è stata fatta! Gli elementi sono stati aggiunti nella dispensa.");
        });

        EditText searchInput = findViewById(R.id.search_input);
        LinearLayout searchButton = findViewById(R.id.search_button);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();

                if (!query.isEmpty()) {
                    List<Item> searchResults = dbHelper.searchShoppingListItemsByName(query);
                    shoppingItemFragment.updateItemList(searchResults);
                } else {
                    // Se il campo è vuoto, ricarica tutti gli elementi della categoria selezionata
                    shoppingItemFragment.loadItems(VerticalMenuFragment.getSelectedCategory());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim().toLowerCase();
            if (!query.isEmpty()) {
                List<Item> searchResults = dbHelper.searchPantryItemsByName(query);
                if (searchResults.isEmpty()) {
                    HomeActivity.showCustomToast(this, "L'elemento è presente nella lista della spesa.");
                } else {
                    HomeActivity.showCustomToast(this, "L'elemento non è presente nella lista della spesa.");
                }
            } else {
                HomeActivity.showCustomToast(this, "Inserisci un nome per cercare.");
                shoppingItemFragment.loadItems(VerticalMenuFragment.getSelectedCategory());
            }
        });
    }

    public void updateCategory(String newCategory) {
        shoppingItemFragment.updateCategory(newCategory);
    }
}
