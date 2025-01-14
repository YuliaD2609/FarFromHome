package com.example.farfromhome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PantryAddProduct extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText editTextProductName;
    private AutoCompleteTextView autoCompleteCategory;
    private ImageView imageViewProduct;
    private TextView textViewQuantity;
    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_item_adder);

        editTextProductName = findViewById(R.id.editTextProductName);
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        Button buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        Button buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);

        // Setup AutoCompleteTextView with an adapter
        String[] categories = {"Category1"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        autoCompleteCategory.setAdapter(adapter);

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

        buttonAddProduct.setOnClickListener(v -> {
            // Handle product addition logic here
        });

        checkPermissions();
    }

    private void selectImage(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                imageViewProduct.setImageURI(selectedImage);
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }
}
