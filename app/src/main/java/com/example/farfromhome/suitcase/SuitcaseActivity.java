package com.example.farfromhome.suitcase;

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
import com.example.farfromhome.Item;
import com.example.farfromhome.menu.HorizontalMenuFragment;
import com.example.farfromhome.R;
import com.example.farfromhome.menu.VerticalMenuFragment;
import com.example.farfromhome.menu.VerticalMenuFragmentSuitcase;
import com.example.farfromhome.pantry.PantryActivity;
import com.example.farfromhome.pantry.PantryAddItem;
import com.example.farfromhome.pantry.PantryItemsFragment;

import java.util.List;

public class SuitcaseActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private SuitcaseItemFragment suitcaseItemFragment;
    private LinearLayout addItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_layout);

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
        bundle.putString("TITLE", "Valigia");
        bundle.putBoolean("SHOW_CART", false);
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        suitcaseItemFragment = new SuitcaseItemFragment();
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            Bundle suitcaseBundle = new Bundle();
            suitcaseBundle.putString("CATEGORY_NAME", categoryName);
            suitcaseItemFragment.setArguments(suitcaseBundle);
        }
        fragmentTransaction.replace(R.id.item_fragment_container, suitcaseItemFragment);

        fragmentTransaction.commit();
    }

    private void setupUI() {
        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(SuitcaseActivity.this, SuitcaseAddItem.class);
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
        if (suitcaseItemFragment != null) {
            suitcaseItemFragment.updateCategory(newCategory);
        }
    }

    public void search(EditText searchInput){
        String query = searchInput.getText().toString().trim().toLowerCase();

        if (query.isEmpty()) {
            HomeActivity.showCustomToast(this, "Inserisci un nome per cercare.");
            suitcaseItemFragment.loadItems(VerticalMenuFragment.getSelectedCategory());
            return;
        }

        String selectedCategory = VerticalMenuFragment.getSelectedCategory();
        List<SuitcaseItem> searchResults = (selectedCategory == null || selectedCategory.isEmpty())
                ? dbHelper.searchSuitcaseListItemsByName(query)
                : dbHelper.searchSuitcaseListItemsByCategoryAndName(selectedCategory, query);

        if (searchResults.isEmpty()) {
            HomeActivity.showCustomToast(this, "Nessun elemento trovato con questo nome.");
        } else {
            suitcaseItemFragment.updateItemList(searchResults);
        }

    }

}
