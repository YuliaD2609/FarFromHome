package com.example.farfromhome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_main);

        // Configura i listener per i pulsanti
        findViewById(R.id.shoppinglistbutton).setOnClickListener(view -> goToShoppingList());
        findViewById(R.id.pantrybutton).setOnClickListener(view -> goToPantry());
        findViewById(R.id.suitcasebutton).setOnClickListener(view -> goToSuitcase());
    }

    private void goToShoppingList() {
        Intent intent = new Intent(this, ShoppingListActivity.class);
        startActivity(intent);
    }

    private void goToPantry() {
        Intent intent = new Intent(this, PantryActivity.class);
        startActivity(intent);
    }

    private void goToSuitcase() {
        Intent intent = new Intent(this, SuitcaseActivity.class);
        startActivity(intent);
    }

    public void goToHome(View view) {
        // In questo esempio, "MainActivity" funge da home. Puoi modificare il nome se necessario.
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // Chiude la activity corrente per evitare ritorno premuto indietro
    }
}
