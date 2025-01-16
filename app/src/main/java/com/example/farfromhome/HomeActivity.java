package com.example.farfromhome;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farfromhome.pantry.PantryActivity;
import com.example.farfromhome.shoppingList.ShoppingListActivity;
import com.example.farfromhome.suitcase.SuitcaseActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DatabasePrefs";
    private static final String KEY_DATABASE_INITIALIZED = "database_initialized";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_main);
        deleteDatabase("farfromhomedb.db");

        checkAndInitializeDatabase();

        View shoppingListButton = findViewById(R.id.shoppinglistbutton);
        View pantryButton = findViewById(R.id.pantrybutton);
        View suitcaseButton = findViewById(R.id.suitcasebutton);
        TextView warningText = findViewById(R.id.warningtext);

        startAnimation(shoppingListButton, 0);
        startAnimation(pantryButton, 300);
        startAnimation(suitcaseButton, 500);
        startAnimation(warningText, 700);

        shoppingListButton.setOnClickListener(view -> goToShoppingList());
        pantryButton.setOnClickListener(view -> goToPantry());
        suitcaseButton.setOnClickListener(view -> goToSuitcase());
    }

    private void checkAndInitializeDatabase() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDatabaseInitialized = preferences.getBoolean(KEY_DATABASE_INITIALIZED, false);

        if (!isDatabaseInitialized) {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            databaseHelper.getWritableDatabase();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_DATABASE_INITIALIZED, true);
            editor.apply();
        }
    }

    private void startAnimation(View view, long delay) {
        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInLeft.setStartOffset(delay);
        view.startAnimation(slideInLeft);
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

    public static void showCustomToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customToastView = inflater.inflate(R.layout.custom_toast, null);

        TextView toastMessage = customToastView.findViewById(R.id.toastMessage);
        toastMessage.setText(message);

        Toast customToast = new Toast(context);
        customToast.setView(customToastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.BOTTOM, 0, 300);
        customToast.show();
    }
}
