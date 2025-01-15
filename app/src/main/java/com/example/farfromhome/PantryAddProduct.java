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

    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private EditText editTextExpiryDate;
    private ImageView imageViewProduct;
    private TextView textViewQuantity;
    private int quantity = 0;
    private Uri selectedImageUri;

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
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        Button buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        Button buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

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

        imageViewProduct.setOnClickListener(this::selectImage);

        buttonAddProduct.setOnClickListener(v -> addProductToDatabase());

    }

    private void selectImage(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageViewProduct.setImageURI(selectedImageUri);
        } else {
            imageViewProduct.setImageResource(R.drawable.home_icon);
        }
    }

    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();
        String expiryDateStr = editTextExpiryDate.getText().toString().trim();
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;
        Date expiryDate = null;

        try {
            expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(expiryDateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Item item = new Item(productName, quantity, null, expiryDate);

        boolean isInserted = databaseHelper.addPantryItem(item);
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
