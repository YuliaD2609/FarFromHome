package com.example.farfromhome;
import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
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
        builder.setTitle("Nuova categoria");

        // Create an EditText to input the category name
        final EditText input = new EditText(this);
        input.setHint("Inserire il nome della categoria");
        input.setTextColor(getResources().getColor(R.color.brown));  // Use your color resource
        builder.setView(input);


        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                if(categoryName.length()>10){
                    Toast.makeText(Categories.this, "Il nome della categoria è troppo lungo", Toast.LENGTH_SHORT).show();
                }else {
                    createCategoryButton(categoryName);
                }
            } else {
                Toast.makeText(Categories.this, "Il nome della categoria non può essere vuoto", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createCategoryButton(String categoryName) {
        // Create a new Button dynamically
        Button newCategoryButton = new Button(this);

        // Convert DP to pixels
        float density = getResources().getDisplayMetrics().density;
        int widthInPixels = (int) (80 * density);  // 80dp to pixels
        int heightInPixels = (int) (50 * density);  // 50dp to pixels
        int bottomMarginInPixels = (int) (5 * density);  // 5dp to pixels

        // Set the button size (Width and Height)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPixels, heightInPixels);
        newCategoryButton.setLayoutParams(params);
        params.bottomMargin = bottomMarginInPixels;

        // Set the text size to 10dp
        newCategoryButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        newCategoryButton.setText(categoryName);

        // Set the background color (light brown)
        newCategoryButton.setBackgroundColor(getResources().getColor(R.color.lightBrown));

        // Set the text color to white
        newCategoryButton.setTextColor(getResources().getColor(R.color.white));

        int buttonId = categoryName.hashCode();
        if (buttonId < 0) {
            buttonId = -buttonId; // Ensure the ID is non-negative
        }
        newCategoryButton.setId(buttonId);

        // Set an OnClickListener for the new button
        newCategoryButton.setOnClickListener(v -> {
            // Call the 'visualizza' method when the button is clicked
            visualizza(categoryName);
        });


        // Add the new button to the category list
        categoryList.addView(newCategoryButton);
    }

    private void visualizza(String categoryName) {
        // Your logic for visualizing the category goes here
        Toast.makeText(Categories.this, "Visualizing category: " + categoryName, Toast.LENGTH_SHORT).show();
    }
}
