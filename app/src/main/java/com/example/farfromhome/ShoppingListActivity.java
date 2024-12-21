package com.example.farfromhome;

import android.app.Activity;
import android.os.Bundle;

public class ShoppingListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_layout); // Layout per la sezione Lista della spesa
    }
}
