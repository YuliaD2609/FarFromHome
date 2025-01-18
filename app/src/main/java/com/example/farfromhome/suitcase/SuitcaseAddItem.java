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
    private String selectedCategory;

    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suitcase_item_adder);

        databaseHelper = new DatabaseHelper(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Valigia");
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        fragmentTransaction.commit();


        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
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
        List<String> categories = databaseHelper.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            System.out.println("No categories found in database.");
            categories.add("N/A");
        } else {
            System.out.println("Categories fetched from database: " + categories);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner, categories) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return super.getDropDownView(position, convertView, parent);
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();

        if(productName.isEmpty()){
            HomeActivity.showCustomToast(this,"Il nome deve essere presente!");
            return;
        }

        if (quantity == 0) {
            HomeActivity.showCustomToast(this,"Non puoi inserire 0 elementi!");
            return;
        }

        selectedCategory = spinnerCategory.getSelectedItem().toString();

        boolean productExists = databaseHelper.doesProductSuitcaseExist(productName);
        if (productExists) {
            HomeActivity.showCustomToast(this,"Un oggetto con questo nome esiste già");
            return;
        }

        SuitcaseItem item = new SuitcaseItem(productName, quantity, selectedCategory);

        boolean isInserted = databaseHelper.addSuitcaseItem(item);
        if (isInserted) {
            inputCleaner();
            HomeActivity.showCustomToast(this,"Prodotto aggiunto con successo!");
            Intent intent = new Intent(this, SuitcaseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            HomeActivity.showCustomToast(this,"Errore nell'aggiunta del prodotto");
        }
    }

    public void inputCleaner(){
        editTextProductName.setText("");
        spinnerCategory.setSelection(0);
        quantity = 0;
        textViewQuantity.setText(String.valueOf(quantity));
    }

}
