package com.example.farfromhome.menu;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.R;
import com.example.farfromhome.pantry.PantryActivity;
import com.example.farfromhome.shoppingList.ShoppingListActivity;
import com.example.farfromhome.suitcase.SuitcaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VerticalMenuFragmentSuitcase extends Fragment {

    private LinearLayout categoryList;
    private List<String> existingCategories = new ArrayList<>();
    private View selectedCategoryView = null;
    DatabaseHelper dbHelper;
    private PantryActivity pantryActivity=null;
    private ShoppingListActivity shoppingActivity=null;
    private SuitcaseActivity suitcaseActivity=null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PantryActivity) {
            pantryActivity = (PantryActivity) context;
        }
        if (context instanceof ShoppingListActivity) {
            shoppingActivity = (ShoppingListActivity) context;
        }
        if (context instanceof SuitcaseActivity) {
            suitcaseActivity = (SuitcaseActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vertical_menu, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        existingCategories = dbHelper.getAllSuitcaseCategories();

        categoryList = rootView.findViewById(R.id.categoryList);

        if (existingCategories.isEmpty()) {
            createCategoryButton("Cucina");
            createCategoryButton("Bagno");
            createCategoryButton("Altro");
        } else {
            for (String cat : existingCategories) {
                createCategoryButton(cat);
            }
        }

        LinearLayout addCategoryButton = rootView.findViewById(R.id.addCategory);
        addCategoryButton.setOnClickListener(this::addSuitcaseCategory);

        return rootView;
    }

    public void addSuitcaseCategory(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        TextView title = new TextView(requireContext());
        title.setText("Nuova categoria");
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.darkerBrown));
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(20, 30, 20, 10);
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);

        FrameLayout container = new FrameLayout(requireContext());
        int margin = (int) (16 * requireContext().getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, margin, margin, margin);

        final EditText input = new EditText(requireContext());
        input.setHint("Inserire il nome della categoria");
        input.setTextColor(getResources().getColor(R.color.darkerBrown));
        input.setHintTextColor(getResources().getColor(R.color.brown));
        input.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkerBrown)));
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                if (categoryName.length() > 8) {
                    HomeActivity.showCustomToast(requireContext(),"Il nome della categoria è troppo lungo");
                } else {
                    if (existingCategories.contains(categoryName)) {
                        HomeActivity.showCustomToast(requireContext(),"Categoria già esistente");
                        return;
                    }
                    createCategoryButton(categoryName);
                }
            } else {
                HomeActivity.showCustomToast(requireContext(),"Il nome della categoria non può essere vuoto");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.popup);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(background);
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkerBrown));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.brown));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
        });
        dialog.show();
    }

    private void createCategoryButton(String categoryName) {
        LinearLayout categoryContainer = new LinearLayout(requireContext());
        categoryContainer.setOrientation(LinearLayout.HORIZONTAL);
        categoryContainer.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (50 * getResources().getDisplayMetrics().density)
        );
        categoryContainer.setLayoutParams(params);
        categoryContainer.setPadding(8, 8, 0, 8);
        categoryContainer.setBackgroundResource(R.drawable.menu_buttons);

        TextView categoryText = new TextView(requireContext());
        categoryText.setText(categoryName);
        categoryText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        categoryText.setTextColor(getResources().getColor(R.color.black));
        categoryText.setGravity(Gravity.CENTER);
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.funneldisplay_bold);
        categoryText.setTypeface(typeface);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textParams.setMargins(0, 0, -70, 0);
        categoryText.setLayoutParams(textParams);
        ImageView deleteIcon = new ImageView(requireContext());
        deleteIcon.setImageResource(R.drawable.x_icon);
        deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (31 * getResources().getDisplayMetrics().density),
                (int) (31 * getResources().getDisplayMetrics().density)
        ));
        deleteIcon.setPadding(0, 0, -30, 70);

        categoryContainer.setOnClickListener(v -> {
            handleCategorySelection(categoryContainer, categoryName);
        });

        deleteIcon.setOnClickListener(v -> {
            confirmAndDeleteCategory(categoryName, categoryContainer);
        });

        categoryContainer.addView(categoryText);
        categoryContainer.addView(deleteIcon);

        categoryList.addView(categoryContainer);

        dbHelper.addSuitcaseCategory(categoryName);
    }

    private void confirmAndDeleteCategory(String categoryName, View categoryContainer) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Elimina Categoria")
                .setMessage("Sei sicuro di voler eliminare la categoria \"" + categoryName + "\"?\nGli elementi al suo interno saranno eliminati.")
                .setPositiveButton("Elimina", (dialog, which) -> {
                    dbHelper.deleteSuitcaseCategory(categoryName);
                    categoryList.removeView(categoryContainer);
                    HomeActivity.showCustomToast(requireContext(), "Categoria eliminata");
                    handleCategorySelection(categoryContainer, null);
                })
                .setNegativeButton("Annulla", (dialog, which) -> {
                    handleCategorySelection(categoryContainer, categoryName);
                })
                .show();
    }




    private void handleCategorySelection(View selectedView, String categoryName) {
        if (selectedCategoryView != null) {
            selectedCategoryView.setBackgroundResource(R.drawable.menu_buttons);
            ((TextView) ((LinearLayout) selectedCategoryView).getChildAt(0))
                    .setTextColor(getResources().getColor(R.color.black));
        }
        if (selectedCategoryView == selectedView) {
            selectedCategoryView = null;
            if (pantryActivity != null)
                pantryActivity.updateCategory(null);
            if (shoppingActivity != null)
                shoppingActivity.updateCategory(null);
            if (suitcaseActivity != null)
                suitcaseActivity.updateCategory(null);
        } else {
            selectedCategoryView = selectedView;
            selectedCategoryView.setBackgroundResource(R.drawable.menu_buttons_selected);
            ((TextView) ((LinearLayout) selectedCategoryView).getChildAt(0))
                    .setTextColor(getResources().getColor(R.color.brown));

            if (pantryActivity != null)
                pantryActivity.updateCategory(categoryName);
            if (shoppingActivity != null)
                shoppingActivity.updateCategory(categoryName);
            if (suitcaseActivity != null)
                suitcaseActivity.updateCategory(categoryName);
        }
    }
}
