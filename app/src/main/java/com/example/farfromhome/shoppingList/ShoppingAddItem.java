package com.example.farfromhome.shoppingList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.menu.HorizontalMenuFragment;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.util.List;

public class ShoppingAddItem extends AppCompatActivity {

    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private TextView textViewQuantity;
    private int quantity = 0;
    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist_item_adder);

        databaseHelper = new DatabaseHelper(this);
        setupUI();
    }

    private void setupUI() {
        setupMenu();
        setupProductNameField();
        setupQuantityButtons();
        setupAddProductButton();
        loadCategories();
    }

    private void setupMenu() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        horizontalFragment.setArguments(createMenuBundle("Lista spesa"));
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);
        fragmentTransaction.commit();
    }

    private Bundle createMenuBundle(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        return bundle;
    }

    private void setupProductNameField() {
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
    }

    private void setupQuantityButtons() {
        textViewQuantity = findViewById(R.id.textViewQuantity);
        LinearLayout buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        LinearLayout buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);

        buttonDecreaseQuantity.setOnClickListener(v -> updateQuantity(-1));
        buttonIncreaseQuantity.setOnClickListener(v -> updateQuantity(1));
    }

    private void setupAddProductButton() {
        LinearLayout buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(v -> addProductToDatabase());
    }

    private void loadCategories() {
        List<String> categories = databaseHelper.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            categories.add("N/A");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, categories);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerCategory.setAdapter(adapter);
    }

    private void updateQuantity(int delta) {
        quantity = Math.max(0, quantity + delta);
        textViewQuantity.setText(String.valueOf(quantity));
    }

    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();

        if (!validateInputs(productName)) return;

        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        if (databaseHelper.doesProductShoppingListExist(productName)) {
            HomeActivity.showCustomToast(this, "Un prodotto con questo nome esiste già");
            return;
        }

        Item item = new Item(productName, quantity, null, selectedCategory);
        boolean isInserted = databaseHelper.addShoppingListItem(item);
        showResultToast(isInserted);
    }

    private boolean validateInputs(String productName) {
        if (productName.isEmpty()) {
            HomeActivity.showCustomToast(this, "Il nome deve essere presente!");
            return false;
        }
        if (quantity == 0) {
            HomeActivity.showCustomToast(this, "Non puoi inserire 0 elementi!");
            return false;
        }
        return true;
    }

    private void showResultToast(boolean isInserted) {
        if (isInserted) {
            resetInputs();
            HomeActivity.showCustomToast(this, "Prodotto aggiunto con successo");
            navigateToShoppingList();
        } else {
            HomeActivity.showCustomToast(this, "Errore nell'aggiunta del prodotto");
        }
    }

    private void resetInputs() {
        editTextProductName.setText("");
        spinnerCategory.setSelection(0);
        quantity = 0;
        textViewQuantity.setText(String.valueOf(quantity));
    }

    private void navigateToShoppingList() {
        Intent intent = new Intent(this, ShoppingListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
