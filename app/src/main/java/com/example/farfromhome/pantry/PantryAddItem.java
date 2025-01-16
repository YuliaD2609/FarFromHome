package com.example.farfromhome.pantry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PantryAddItem extends AppCompatActivity {


    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private EditText editTextExpiryDate;
    private TextView textViewQuantity;
    private int quantity = 0;
    private String selectedCategory;

    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
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



        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        addDateInputFormat(editTextExpiryDate);
        editTextProductName = findViewById(R.id.editTextProductName);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        loadCategories();

        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
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

    private void addDateInputFormat(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private final String dateFormat = "dd/MM/yyyy";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String input = s.toString();
                StringBuilder formattedInput = new StringBuilder();

                // Rimuovere qualsiasi carattere che non sia un numero
                input = input.replaceAll("[^\\d]", "");

                int length = input.length();
                if (length > 2) {
                    formattedInput.append(input.substring(0, 2)).append("/");
                    if (length > 4) {
                        formattedInput.append(input.substring(2, 4)).append("/");
                        formattedInput.append(input.substring(4, Math.min(8, length)));
                    } else {
                        formattedInput.append(input.substring(2));
                    }
                } else {
                    formattedInput.append(input);
                }

                editText.removeTextChangedListener(this); // Rimuove temporaneamente il watcher
                editText.setText(formattedInput.toString());
                editText.setSelection(formattedInput.length());
                editText.addTextChangedListener(this); // Riaggiunge il watcher

                isFormatting = false;
            }
        });
    }


    private void addProductToDatabase() {
        if(quantity == 0){
            Toast.makeText(this, "Non puoi inserire 0 elememti!", Toast.LENGTH_SHORT).show();
            return;
        }
        String productName = editTextProductName.getText().toString().trim();
        String expiryDateStr = editTextExpiryDate.getText().toString().trim();
        Date expiryDate = null;

        try {
            expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(expiryDateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedCategory = spinnerCategory.getSelectedItem().toString();

        Item item = new Item(productName, quantity, expiryDate,selectedCategory);

        boolean isInserted = databaseHelper.addPantryItem(item);
        if (isInserted) {
            inputCleaner();
            Toast.makeText(this, "Prodotto aggiunto con successo!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PantryActivity.class);
            intent.putExtra("CATEGORY_NAME", selectedCategory);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Errore nell'aggiunta del prodotto.", Toast.LENGTH_SHORT).show();
        }
    }

    public void inputCleaner(){
        editTextProductName.setText("");
        editTextExpiryDate.setText("");
        spinnerCategory.setSelection(0);
        quantity = 0;
        textViewQuantity.setText(String.valueOf(quantity));
    }

}
