package com.example.farfromhome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import androidx.appcompat.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.farfromhome.pantry.PantryActivity;
import com.example.farfromhome.shoppingList.ShoppingListActivity;
import com.example.farfromhome.suitcase.SuitcaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DatabasePrefs";
    private static final String KEY_DATABASE_INITIALIZED = "database_initialized";
    private static final String CHANNEL_ID = "expiring_products_channel";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private DatabaseHelper databaseHelper;
    private LinearLayout warningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_main);

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
        ImageView notificationButton = findViewById(R.id.notification_icon);
        warningText = findViewById(R.id.warningLayout);

        databaseHelper = new DatabaseHelper(this);

        adjustLayout();

        startAnimation(shoppingListButton, 0);
        startAnimation(pantryButton, 300);
        startAnimation(suitcaseButton, 500);
        startAnimation(notificationButton, 600);
        startBottomAnimation(warningText, 200);

        loadExpiringProducts();

        shoppingListButton.setOnClickListener(view -> goToShoppingList());
        pantryButton.setOnClickListener(view -> goToPantry());
        suitcaseButton.setOnClickListener(view -> goToSuitcase());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                showCustomToast(this, "Abilita il permesso di allarme per ricevere i promemoria di scadenza! (Notifica in alto a destra)");
            }
        }

        notificationButton.setOnClickListener(v -> {
                showNotificationTimePicker();
        });
    }



    public void showNotificationTimePicker() {

        // Verifica se l'app ha i permessi per programmare allarmi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        // Creazione del dialogo per impostare l'orario della notifica
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText("Imposta orario notifica");
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.darkerBrown));
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(20, 30, 20, 10);
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(50, 40, 50, 10);
        layout.setGravity(Gravity.CENTER);

        EditText hourInput = new EditText(this);
        hourInput.setHint("HH");
        hourInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        hourInput.setGravity(Gravity.CENTER);
        hourInput.setTextSize(18);
        hourInput.setTextColor(getResources().getColor(R.color.darkerBrown));
        hourInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        hourInput.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown)));
        hourInput.setHintTextColor(getResources().getColor(R.color.lightBrown));

        TextView separator = new TextView(this);
        separator.setText(" : ");
        separator.setTextSize(18);
        separator.setTextColor(getResources().getColor(R.color.brown));

        EditText minuteInput = new EditText(this);
        minuteInput.setHint("MM");
        minuteInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        minuteInput.setGravity(Gravity.CENTER);
        minuteInput.setTextSize(18);
        minuteInput.setTextColor(getResources().getColor(R.color.darkerBrown));
        minuteInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        minuteInput.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown)));
        minuteInput.setHintTextColor(getResources().getColor(R.color.lightBrown));

        layout.addView(hourInput);
        layout.addView(separator);
        layout.addView(minuteInput);
        builder.setView(layout);

        builder.setPositiveButton("Salva", (dialog, which) -> {
            String hourText = hourInput.getText().toString().trim();
            String minuteText = minuteInput.getText().toString().trim();

            if (isValidTime(hourText, minuteText)) {
                int hour = Integer.parseInt(hourText);
                int minute = Integer.parseInt(minuteText);
                saveNotificationTime(hour, minute);
            } else {
                showCustomToast(this, "Inserisci un orario valido (00-23 : 00-59)");
            }
        });

        builder.setNegativeButton("Annulla", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        Drawable background = ContextCompat.getDrawable(this, R.drawable.popup);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(background);

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkerBrown));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.brown));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
        });

        dialog.show();
    }


    private boolean isValidTime(String hourText, String minuteText) {
        if (hourText.isEmpty() || minuteText.isEmpty()) return false;

        try {
            int hour = Integer.parseInt(hourText);
            int minute = Integer.parseInt(minuteText);
            return (hour >= 0 && hour <= 23) && (minute >= 0 && minute <= 59);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveNotificationTime(int hour, int minute) {
        SharedPreferences prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notification_hour", hour);
        editor.putInt("notification_minute", minute);
        editor.apply();
        scheduleDailyNotification(hour, minute);
        showCustomToast(this, "Orario notifiche impostato: " + hour + ":" + String.format("%02d", minute));
    }

    private void scheduleDailyNotification(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void adjustLayout() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;

        ImageView title = findViewById(R.id.title);
        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) title.getLayoutParams();
        titleParams.height = (int) (screenHeight * 0.15);
        title.setLayoutParams(titleParams);

        LinearLayout warningLayout = findViewById(R.id.warningLayout);
        LinearLayout.LayoutParams warningParams = (LinearLayout.LayoutParams) warningLayout.getLayoutParams();
        warningParams.height = (int) (screenHeight * 0.25);
        warningLayout.setLayoutParams(warningParams);

        int buttonSpacing = (int) (screenHeight * 0.05);

        LinearLayout shoppingListButton = findViewById(R.id.shoppinglistbutton);
        LinearLayout pantryButton = findViewById(R.id.pantrybutton);
        LinearLayout suitcaseButton = findViewById(R.id.suitcasebutton);

        LinearLayout.LayoutParams shoppingParams = (LinearLayout.LayoutParams) shoppingListButton.getLayoutParams();
        shoppingParams.setMargins(0, 0, 0, buttonSpacing);
        shoppingListButton.setLayoutParams(shoppingParams);

        LinearLayout.LayoutParams pantryParams = (LinearLayout.LayoutParams) pantryButton.getLayoutParams();
        pantryParams.setMargins(0, 0, 0, buttonSpacing);
        pantryButton.setLayoutParams(pantryParams);

        LinearLayout.LayoutParams suitcaseParams = (LinearLayout.LayoutParams) suitcaseButton.getLayoutParams();
        suitcaseParams.setMargins(0, 0, 0, buttonSpacing);
        suitcaseButton.setLayoutParams(suitcaseParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean checkAndRequestPermissions() {
        boolean hasNotificationPermission = true;
        boolean hasLocationPermission = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }

        hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasNotificationPermission || !hasLocationPermission) {
            String[] permissionsToRequest = new String[]{
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean notificationAccepted = false;
            boolean locationAccepted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.POST_NOTIFICATIONS)) {
                    notificationAccepted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    locationAccepted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            if (!notificationAccepted && !locationAccepted) {
                showPermissionDeniedMessage(notificationAccepted, locationAccepted);
            } else {

            }
        }
    }

    private void showPermissionDeniedMessage(boolean notificationAccepted, boolean locationAccepted) {
        if (!notificationAccepted) {
            showCustomToast(this, "Il permesso di notifica è richiesto per il corretto funzionamento dell'app");
        }
        if (!locationAccepted) {
            showCustomToast(this, "Il permesso di posizione è richiesto per il corretto funzionamento dell'app");
        }
    }

    private void initializeSystem() {
        setContentView(R.layout.homepage_main);
    }

    private void loadExpiringProducts() {
        new Handler().post(() -> {
            List<Item> pantryItems = databaseHelper.getAllPantryItems();
            LinearLayout warningContainer = findViewById(R.id.warningContainer);
            warningContainer.removeAllViews();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            boolean hasExpiringItems = false;

            for (Item item : pantryItems) {
                Date expiryDate = item.getExpiry();
                if (expiryDate != null) {
                    long diffInMillis = expiryDate.getTime() - calendar.getTimeInMillis();
                    long daysToExpiry = diffInMillis / (1000 * 60 * 60 * 24);

                    if (daysToExpiry >= 0 && daysToExpiry <= 14) {
                        hasExpiringItems = true;

                        LinearLayout row = new LinearLayout(this);
                        row.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        row.setOrientation(LinearLayout.HORIZONTAL);

                        TextView itemNameView = new TextView(this);
                        itemNameView.setLayoutParams(new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                        itemNameView.setText("• " + item.getName());
                        itemNameView.setTextSize(16);
                        itemNameView.setTextColor(getResources().getColor(R.color.black));

                        TextView expiryDateView = new TextView(this);
                        expiryDateView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        expiryDateView.setText(sdf.format(expiryDate));
                        expiryDateView.setTextSize(16);
                        expiryDateView.setTextColor(getResources().getColor(R.color.black));

                        row.addView(itemNameView);
                        row.addView(expiryDateView);
                        warningContainer.addView(row);

                        scheduleNotification(this, expiryDate.getTime(), 7);
                        scheduleNotification(this, expiryDate.getTime(), 2);
                        scheduleNotification(this, expiryDate.getTime(), 1);
                    }
                }
            }

            if (!hasExpiringItems) {
                TextView noItemsView = new TextView(this);
                noItemsView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                noItemsView.setText("Non ci sono elementi in scadenza");
                noItemsView.setTextSize(16);
                noItemsView.setTextColor(getResources().getColor(R.color.black));
                noItemsView.setGravity(Gravity.CENTER);
                warningContainer.addView(noItemsView);
            }
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Context context, long expiryTime, int daysBefore) {
        long triggerTime = expiryTime - (daysBefore * 24 * 60 * 60 * 1000);

        if (triggerTime > System.currentTimeMillis()) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) triggerTime, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    } else {
                        Log.e("Alarm", "L'app non ha il permesso per allarmi esatti");
                    }
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            }
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

    private void startBottomAnimation(View view, long delay) {
        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadExpiringProducts();
    }
}
