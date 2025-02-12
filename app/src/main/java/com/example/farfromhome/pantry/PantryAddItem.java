package com.example.farfromhome.pantry;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.menu.HorizontalMenuFragment;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryAddItem extends AppCompatActivity {

    private EditText editTextProductName, editTextExpiryDate;
    private Spinner spinnerCategory;
    private TextView textViewQuantity;
    private int quantity = 0;
    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_item_adder);

        databaseHelper = new DatabaseHelper(this);
        setupUI();
        loadCategories();
    }

    private void setupUI() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        horizontalFragment.setArguments(getTitleBundle("Dispensa"));
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment).commit();

        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextExpiryDate.setOnClickListener(v -> showDatePicker());
        setEditTextStyle(editTextExpiryDate);

        editTextProductName = findViewById(R.id.editTextProductName);
        setEditTextStyle(editTextProductName);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        textViewQuantity = findViewById(R.id.textViewQuantity);

        findViewById(R.id.buttonDecreaseQuantity).setOnClickListener(v -> updateQuantity(-1));
        findViewById(R.id.buttonIncreaseQuantity).setOnClickListener(v -> updateQuantity(1));
        findViewById(R.id.buttonAddProduct).setOnClickListener(v -> addProductToDatabase());
    }

    private Bundle getTitleBundle(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        return bundle;
    }

    private void setEditTextStyle(EditText editText) {
        editText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
    }

    private void updateQuantity(int delta) {
        quantity = Math.max(0, quantity + delta);
        textViewQuantity.setText(String.valueOf(quantity));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.CustomDatePickerDialog,
                (view, year, month, dayOfMonth) -> setDateField(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, 15);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setDateField(int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, dayOfMonth);
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.getTime());
        editTextExpiryDate.setText(new SpannableString(formattedDate));
        setEditTextStyle(editTextExpiryDate);
    }

    private void loadCategories() {
        List<String> categories = databaseHelper.getAllCategories();
        if (categories == null || categories.isEmpty()) categories.add("N/A");

        spinnerCategory.setAdapter(new ArrayAdapter<>(this, R.layout.custom_spinner, categories));
    }

    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();
        String expiryDateStr = editTextExpiryDate.getText().toString().trim();
        Date expiryDate=null;

        if (!validateInputs(productName, expiryDateStr)) return;
        if(!expiryDateStr.isEmpty())
            expiryDate = parseDate(expiryDateStr);

        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        if (databaseHelper.doesProductPantryExist(productName)) {
            HomeActivity.showCustomToast(this, "Un prodotto con questo nome esiste già!");
            return;
        }

        boolean isInserted = databaseHelper.addPantryItem(new Item(productName, quantity, expiryDate, selectedCategory));
        showResultToast(isInserted);
    }

    private boolean validateInputs(String productName, String expiryDateStr) {
        if (productName.isEmpty()) {
            HomeActivity.showCustomToast(this, "Il nome deve essere presente!");
            return false;
        }
        if (quantity == 0) {
            HomeActivity.showCustomToast(this, "Non puoi inserire 0 elementi!");
            return false;
        }

        return true;
    }

    private Date parseDate(String expiryDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            Date expiryDate = sdf.parse(expiryDateStr);

            if (!validateDateRange(expiryDate)) return null;

            return expiryDate;
        } catch (Exception e) {
            HomeActivity.showCustomToast(this, "Formato data non valido.");
            return null;
        }
    }

    private boolean validateDateRange(Date expiryDate) {
        Date currentDate = new Date();

        if (expiryDate.before(currentDate)) {
            HomeActivity.showCustomToast(this, "La data di scadenza deve essere maggiore della data attuale.");
            return false;
        }

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 15);

        if (expiryDate.after(maxDate.getTime())) {
            HomeActivity.showCustomToast(this, "La data di scadenza non può essere oltre 15 anni da oggi.");
            return false;
        }
        return true;
    }

    private void showResultToast(boolean isInserted) {
        if (isInserted) {
            resetInputs();
            HomeActivity.showCustomToast(this, "Prodotto aggiunto con successo!");
            startActivity(new Intent(this, PantryActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        } else {
            HomeActivity.showCustomToast(this, "Errore nell'aggiunta del prodotto");
        }
    }

    private void resetInputs() {
        editTextProductName.setText("");
        editTextExpiryDate.setText("");
        spinnerCategory.setSelection(0);
        updateQuantity(-quantity);
    }
}
