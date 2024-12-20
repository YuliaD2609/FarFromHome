package com.example.farfromhome;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class Categories extends Activity {

    private LinearLayout categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_menu);

        categoryList = findViewById(R.id.categoryList);
    }

    public void addCategory(View v) {
        // Create an AlertDialog with an input field
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        // Create an EditText to input the category name
        final EditText input = new EditText(this);
        input.setHint("Enter category name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                createCategoryButton(categoryName);
            } else {
                Toast.makeText(Categories.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createCategoryButton(String categoryName) {
        // Create a new Button dynamically
        Button newCategoryButton = new Button(this);
        newCategoryButton.setText(categoryName);
        newCategoryButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newCategoryButton.setBackgroundColor(getResources().getColor(R.color.brown));
        newCategoryButton.setTextColor(getResources().getColor(R.color.white));

        // Set an OnClickListener for the new button
        newCategoryButton.setOnClickListener(v -> {
            Toast.makeText(Categories.this, "Clicked: " + categoryName, Toast.LENGTH_SHORT).show();
            // Add navigation logic for the new category here
        });

        // Add the new button to the category list
        categoryList.addView(newCategoryButton);
    }
}
