package com.example.farfromhome;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShoppingList extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_layout);
        TextView titolo= findViewById(R.id.title);
        titolo.setText("Lista della spesa");
    }
}
