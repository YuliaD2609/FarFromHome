package com.example.farfromhome;

import static androidx.core.content.ContentProviderCompat.requireContext;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.farfromhome.pantry.PantryActivity;
import com.example.farfromhome.shoppingList.ShoppingListActivity;
import com.example.farfromhome.suitcase.SuitcaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DatabasePrefs";
    private static final String KEY_DATABASE_INITIALIZED = "database_initialized";
    private static final String CHANNEL_ID = "expiring_products_channel";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private DatabaseHelper databaseHelper;
    private TextView warningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_main);

        //deleteDatabase("farfromhomedb.db");

        checkAndInitializeDatabase();
        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkAndRequestPermissions()) {
                initializeSystem();
            }
        }

        View shoppingListButton = findViewById(R.id.shoppinglistbutton);
        View pantryButton = findViewById(R.id.pantrybutton);
        View suitcaseButton = findViewById(R.id.suitcasebutton);
        warningText = findViewById(R.id.warningtext);

        databaseHelper = new DatabaseHelper(this);

        startAnimation(shoppingListButton, 0);
        startAnimation(pantryButton, 300);
        startAnimation(suitcaseButton, 500);
        startAnimation(warningText, 700);

        loadExpiringProducts();

        shoppingListButton.setOnClickListener(view -> goToShoppingList());
        pantryButton.setOnClickListener(view -> goToPantry());
        suitcaseButton.setOnClickListener(view -> goToSuitcase());
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean checkAndRequestPermissions() {
        boolean hasStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean hasNotificationPermission = true;

        // Controlla il permesso di notifica solo su Android 13 e successivi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }

        if (!hasStoragePermission || !hasNotificationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean notificationAccepted = (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) || grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (storageAccepted && notificationAccepted) {
                    initializeSystem();
                } else {
                    // I permessi non sono stati concessi, informa l'utente
                    showPermissionDeniedMessage();
                }
            }
        }
    }

    private void initializeSystem() {
        setContentView(R.layout.homepage_main);
        // Avvia il sistema qui
    }

    private void showPermissionDeniedMessage() {
        HomeActivity.showCustomToast(this,"I permessi di archivio e notifica sono richiesti per il corretto funzionamento dell'app");
    }

    private void loadExpiringProducts() {
        new Handler().post(() -> {
            List<Item> pantryItems = databaseHelper.getAllPantryItems();
            StringBuilder warnings = new StringBuilder("Prodotti in scadenza:\n");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            boolean notify = false;

            for (Item item : pantryItems) {
                Date expiryDate =item.getExpiry();
                long diffInMillis = expiryDate.getTime() - calendar.getTimeInMillis();
                long daysToExpiry = diffInMillis / (1000 * 60 * 60 * 24);

                if (daysToExpiry >= 0 && daysToExpiry <= 14) {
                    warnings.append(item.getName()).append(" - mancano ")
                            .append(daysToExpiry).append(" giorni alla scadenza\n");

                    if (daysToExpiry <= 7) {
                        notify = true;
                    }
                }
            }

            warningText.setText(warnings.toString());

            if (notify) {
                sendNotification();
            }
        });
    }

    private void sendNotification() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Attenzione!")
                .setContentText("Hai dei prodotti con scadenza a breve!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expiring Products Channel";
            String description = "Channel for expiring products notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
