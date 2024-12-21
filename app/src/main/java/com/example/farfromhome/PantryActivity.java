package com.example.farfromhome;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PantryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_layout);
        TextView titolo= findViewById(R.id.title);
        titolo.setText("Dispensa");
    }
}
