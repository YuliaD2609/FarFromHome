package com.example.farfromhome.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.HorizontalMenuFragment;
import com.example.farfromhome.R;
import com.example.farfromhome.VerticalMenuFragmentSuitcase;

public class SuitcaseActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private SuitcaseItemFragment suitcaseItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suitcase_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VerticalMenuFragmentSuitcase categoriesFragment = new VerticalMenuFragmentSuitcase();
        fragmentTransaction.replace(R.id.vertical_menu, categoriesFragment);

        HorizontalMenuFragment horizontalFragment = new HorizontalMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Valigia");
        horizontalFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.horizontal_menu, horizontalFragment);

        dbHelper = new DatabaseHelper(this);

        SuitcaseItem i=new SuitcaseItem("prova", 3, "Cucina");
        dbHelper.addSuitcaseItem(i);


        suitcaseItemFragment = new SuitcaseItemFragment();
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            Bundle suitcaseBundle = new Bundle();
            suitcaseBundle.putString("CATEGORY_NAME", categoryName);
            suitcaseItemFragment.setArguments(suitcaseBundle);
        }
        fragmentTransaction.replace(R.id.suitcaseItemList, suitcaseItemFragment);

        fragmentTransaction.commit();

        LinearLayout addItemButton = findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(v -> {
         Intent intent = new Intent(SuitcaseActivity.this, SuitcaseAddItem.class);
         startActivity(intent);
        });

        LinearLayout suitcaseDoneButton = findViewById(R.id.suitcaseDone);
        suitcaseDoneButton.setOnClickListener(v -> {
            suitcaseItemFragment.removeMarkedItems();
        });
    }

    public void updateCategory(String newCategory) {
        if (suitcaseItemFragment != null) {
            suitcaseItemFragment.updateCategory(newCategory);
        }
    }

}
