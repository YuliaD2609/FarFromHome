package com.example.farfromhome;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VerticalMenuFragment extends Fragment {

    private LinearLayout categoryList;
    private List<String> existingCategories=new ArrayList<>();
    DatabaseHelper dbHelper;
    private PantryActivity pantryActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PantryActivity) {
            pantryActivity = (PantryActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vertical_menu, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        existingCategories = dbHelper.getAllCategories();

        categoryList = rootView.findViewById(R.id.categoryList);

        if(existingCategories.isEmpty()){
            createCategoryButton("Cucina");
            createCategoryButton("Bagno");
            createCategoryButton("Frigo");
            createCategoryButton("Altro");
        }else{
            List<String> categoriesCopy = new ArrayList<>(existingCategories);
            for (String cat : categoriesCopy) {
                createCategoryButton(cat);
            }
        }


        Button addCategoryButton = rootView.findViewById(R.id.addCategory);
        addCategoryButton.setOnClickListener(this::addCategory);

        return rootView;
    }

    public void addCategory(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nuova categoria");

        final EditText input = new EditText(requireContext());
        input.setHint("Inserire il nome della categoria");
        input.setTextColor(getResources().getColor(R.color.black));
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                if (categoryName.length() > 10) {
                    Toast.makeText(requireContext(), "Il nome della categoria è troppo lungo", Toast.LENGTH_SHORT).show();
                } else {
                    if (existingCategories.contains(categoryName)) {
                        Toast.makeText(requireContext(), "Categoria già esistente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createCategoryButton(categoryName);
                }
            } else {
                Toast.makeText(requireContext(), "Il nome della categoria non può essere vuoto", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background_beige);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(background);

        dialog.show();
    }

    private void createCategoryButton(String categoryName) {
        Button newCategoryButton = new Button(requireContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (50 * getResources().getDisplayMetrics().density)
        );
        params.bottomMargin = (int) (5 * getResources().getDisplayMetrics().density);
        newCategoryButton.setLayoutParams(params);

        newCategoryButton.setText(categoryName);
        newCategoryButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        newCategoryButton.setBackgroundResource(R.drawable.menu_buttons);
        newCategoryButton.setTextColor(getResources().getColor(R.color.lightBrown));
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.funneldisplay_bold);
        newCategoryButton.setTypeface(typeface);
        newCategoryButton.setPadding(0, 0, 0, 0);

        int buttonId = categoryName.hashCode();
        if (buttonId < 0) {
            buttonId = -buttonId;
        }
        newCategoryButton.setId(buttonId);

        newCategoryButton.setOnClickListener(v -> {
            visualizza(categoryName);
        });

        dbHelper.addCategory(categoryName);
        existingCategories.add(categoryName);

        categoryList.addView(newCategoryButton);
    }

    private void visualizza(String categoryName) {
        if (pantryActivity != null) {
            pantryActivity.updateCategory(categoryName);
        }
    }

}
