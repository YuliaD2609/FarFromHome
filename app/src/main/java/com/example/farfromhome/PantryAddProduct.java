package com.example.farfromhome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PantryAddProduct extends AppCompatActivity {


    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private EditText editTextExpiryDate;
    private TextView textViewQuantity;
    private int quantity = 0;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_item_adder);

        databaseHelper = new DatabaseHelper(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Dispensa");
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        fragmentTransaction.commit();


        editTextProductName = findViewById(R.id.editTextProductName);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        loadCategories();

        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        Button buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        Button buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);

        buttonDecreaseQuantity.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                textViewQuantity.setText(String.valueOf(quantity));
            }
        });

        buttonIncreaseQuantity.setOnClickListener(v -> {
            quantity++;
            textViewQuantity.setText(String.valueOf(quantity));
        });

        buttonAddProduct.setOnClickListener(v -> addProductToDatabase());

    }

    private void loadCategories() {
        // Fetch categories from the database
        List<String> categories = databaseHelper.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            System.out.println("No categories found in database.");
            // Add a default category or handle the empty state if necessary
            categories.add("N/A");
        } else {
            System.out.println("Categories fetched from database: " + categories);
        }

        // Populate spinner with categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();
        String expiryDateStr = editTextExpiryDate.getText().toString().trim();
        Date expiryDate = null;

        try {
            expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(expiryDateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        Item item = new Item(productName, quantity, expiryDate);

        boolean isInserted = databaseHelper.addPantryItem(item,selectedCategory);
        if (isInserted) {
            finish(); // Close the activity
            List<Item> listpantry= databaseHelper.getAllPantryItems();
            System.out.println(listpantry.toString());
            System.out.println("sdhbfihusfdvjnisd");
        } else {
            // Handle error
        }
    }
}
