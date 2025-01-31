package com.example.farfromhome.pantry;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
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
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        editTextExpiryDate.setOnClickListener(v -> showDatePicker(editTextExpiryDate));
        editTextExpiryDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));

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

    public void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.CustomDatePickerDialog,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    SpannableString underlineExpiry = new SpannableString(formattedDate);

                    editText.setText(underlineExpiry);
                    editText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                },
                currentYear, currentMonth, currentDay
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, 15);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
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

                editText.removeTextChangedListener(this);
                editText.setText(formattedInput.toString());
                editText.setSelection(formattedInput.length());
                editText.addTextChangedListener(this);

                isFormatting = false;
            }
        });
    }


    private void addProductToDatabase() {
        String productName = editTextProductName.getText().toString().trim();
        String expiryDateStr = editTextExpiryDate.getText().toString().trim();
        Date expiryDate = null;

        if (!expiryDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                expiryDate = sdf.parse(expiryDateStr);

                Date currentDate = new Date();

                if (expiryDate.before(currentDate)) {
                    HomeActivity.showCustomToast(this, "La data di scadenza deve essere maggiore della data attuale.");
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.YEAR, 15);
                Date maxDate = calendar.getTime();

                if (expiryDate.after(maxDate)) {
                    HomeActivity.showCustomToast(this, "La data di scadenza non può essere oltre 15 anni da oggi.");
                    return;
                }

                Calendar expiryCalendar = Calendar.getInstance();
                expiryCalendar.setTime(expiryDate);
                int day = expiryCalendar.get(Calendar.DAY_OF_MONTH);
                int month = expiryCalendar.get(Calendar.MONTH) + 1;

                if (day < 1 || day > 31) {
                    HomeActivity.showCustomToast(this, "Il giorno deve essere compreso tra 1 e 31.");
                    return;
                }

                if (month < 1 || month > 12) {
                    HomeActivity.showCustomToast(this, "Il mese deve essere compreso tra 1 e 12.");
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                HomeActivity.showCustomToast(this, "Formato data non valido.");
                return;
            }
        }

        if(productName.isEmpty()){
            HomeActivity.showCustomToast(this,"Il nome deve essere presente!");
            return;
        }

        if (quantity == 0) {
            HomeActivity.showCustomToast(this, "Non puoi inserire 0 elementi!");
            return;
        }

        selectedCategory = spinnerCategory.getSelectedItem().toString();

        boolean productExists = databaseHelper.doesProductPantryExist(productName);
        if (productExists) {
            HomeActivity.showCustomToast(this, "Un prodotto con questo nome esiste già!");
            return;
        }

        Item item = new Item(productName, quantity, expiryDate, selectedCategory);

        boolean isInserted = databaseHelper.addPantryItem(item);
        if (isInserted) {
            inputCleaner();
            HomeActivity.showCustomToast(this,"Prodotto aggiunto con successo!");
            Intent intent = new Intent(this, PantryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            HomeActivity.showCustomToast(this,"Errore nell'aggiunta del prodotto");
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
