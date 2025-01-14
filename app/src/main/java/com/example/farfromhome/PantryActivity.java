package com.example.farfromhome;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends Activity {

    private RecyclerView itemList;
    private FloatingActionButton addItemButton;
    private ItemAdapter itemAdapter;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantry_layout);

        TextView titolo= findViewById(R.id.title);
        titolo.setText("Dispensa");

        itemList = findViewById(R.id.itemList);
        addItemButton = findViewById(R.id.addItemButton);

        // Setup RecyclerView
        items = new ArrayList<>(); // Load items from database here
        itemAdapter = new ItemAdapter(items);
        itemList.setLayoutManager(new LinearLayoutManager(this));
        itemList.setAdapter(itemAdapter);

        // Add Item Button Click Listener
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding a new item
                Toast.makeText(PantryActivity.this, "Add Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update item quantity logic in ItemAdapter
    public void updateItemQuantity(int position, int change) {
        Item item = items.get(position);
        int newQuantity = item.getQuantity() + change;
        if (newQuantity >= 0) {
            item.setQuantity(newQuantity);
            itemAdapter.notifyItemChanged(position);
        }
    }
}
