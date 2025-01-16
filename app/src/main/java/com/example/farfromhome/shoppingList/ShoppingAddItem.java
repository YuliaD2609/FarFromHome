package com.example.farfromhome.shoppingList;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.farfromhome.pantry.PantryActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShoppingAddItem extends AppCompatActivity {


    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private TextView textViewQuantity;
    private int quantity = 0;
    private String selectedCategory;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();
        Date expiryDate = null;


        // Check if quantity is 0
        if (quantity == 0) {
            Toast.makeText(this, "Non puoi inserire 0 elementi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve selected category
        selectedCategory = spinnerCategory.getSelectedItem().toString();

        // Check if the product name already exists in the database
        boolean productExists = databaseHelper.doesProductShoppingListExist(productName);
        if (productExists) {
            Toast.makeText(this, "Un prodotto con questo nome esiste già!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Item instance
        Item item = new Item(productName, quantity, expiryDate, selectedCategory);

        // Insert the item into the database
        boolean isInserted = databaseHelper.addShoppingListItem(item);
        if (isInserted) {
            inputCleaner();
            Toast.makeText(this, "Prodotto aggiunto con successo!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ShoppingListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Errore nell'aggiunta del prodotto.", Toast.LENGTH_SHORT).show();
        }
    }

    public void inputCleaner(){
        editTextProductName.setText("");
        spinnerCategory.setSelection(0);
        quantity = 0;
        textViewQuantity.setText(String.valueOf(quantity));
    }

}
