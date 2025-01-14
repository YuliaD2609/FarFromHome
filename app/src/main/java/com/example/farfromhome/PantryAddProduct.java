package com.example.farfromhome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PantryAddProduct extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText editTextProductName;
    private Spinner spinnerCategory;
    private ImageView imageViewProduct;
    private TextView textViewQuantity;
    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_item_adder);

        editTextProductName = findViewById(R.id.editTextProductName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        Button buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);
        Button buttonIncreaseQuantity = findViewById(R.id.buttonIncreaseQuantity);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);

        // Set up Spinner
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

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Immagine selezionata dall'utente
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ImageView imageView = findViewById(R.id.itemImage);
                imageView.setImageURI(selectedImageUri);
            }
        } else {
            // Nessuna immagine selezionata, usa l'immagine di default
            ImageView imageView = findViewById(R.id.itemImage);
            imageView.setImageResource(R.drawable.home_icon);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);
        }
    }
}
