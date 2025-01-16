package com.example.farfromhome.shoppingList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HorizontalMenuFragment;
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Lista spesa");
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        fragmentTransaction.commit();


        editTextProductName = findViewById(R.id.editTextProductName);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        loadCategories();

        textViewQuantity = findViewById(R.id.textViewQuantity);
        LinearLayout buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        LinearLayout buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        LinearLayout buttonAddProduct = findViewById(R.id.buttonAddProduct);

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
        if(quantity == 0){
            Toast.makeText(this, "Non puoi inserire 0 elememti!", Toast.LENGTH_SHORT).show();
            return;
        }
        String productName = editTextProductName.getText().toString().trim();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        Item item = new Item(productName, quantity, null,selectedCategory);

        boolean isInserted = databaseHelper.addShoppingListItem(item);
        if (isInserted) {
            finish(); // Close the activity
            List<Item> list= databaseHelper.getAllShoppingListItems();
            System.out.println(list.toString());
            System.out.println("sdhbfihusfdvjnisd");
        } else {
            // Handle error
        }
    }
}
