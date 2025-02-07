package com.example.farfromhome.suitcase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.farfromhome.R;

import java.util.List;

public class SuitcaseAddItem extends AppCompatActivity {

    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private TextView textViewQuantity;
    private int quantity = 0;
    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suitcase_item_adder);

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
        horizontalFragment.setArguments(createMenuBundle("Valigia"));
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
        List<String> categories = databaseHelper.getAllSuitcaseCategories();
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
        if (databaseHelper.doesProductSuitcaseExist(productName)) {
            HomeActivity.showCustomToast(this, "Un oggetto con questo nome esiste già");
            return;
        }

        SuitcaseItem item = new SuitcaseItem(productName, quantity, selectedCategory);
        boolean isInserted = databaseHelper.addSuitcaseItem(item);
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
            HomeActivity.showCustomToast(this, "Prodotto aggiunto con successo!");
            navigateToSuitcaseList();
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

    private void navigateToSuitcaseList() {
        Intent intent = new Intent(this, SuitcaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
