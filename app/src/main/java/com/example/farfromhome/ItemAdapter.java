package com.example.farfromhome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        // Set image visibility based on availability
        if (item.getImageResource() != null) {
            holder.itemImage.setVisibility(View.VISIBLE);
            holder.itemImage.setImageResource(item.getImageResource());
        } else {
            holder.itemImage.setVisibility(View.GONE);
        }

        // Increment Button
        holder.incrementButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
        });

        // Decrement Button
        holder.decrementButton.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        Button incrementButton, decrementButton;
        ImageView itemImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}



//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;

//import androidx.recyclerview.widget.RecyclerView;

//public class ItemAdapter /*extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>*/ {}
/*
    private List<Item> items;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.quantityDisplay.setText(String.valueOf(item.getQuantity()));

        // Set color of the square (can be any color)
        holder.colorableSquare.setBackgroundColor(item.getColor());

        // Set click listeners for buttons to change quantity
        holder.incrementButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.quantityDisplay.setText(String.valueOf(item.getQuantity()));
        });

        holder.decrementButton.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                holder.quantityDisplay.setText(String.valueOf(item.getQuantity()));
            }
        });

        // Set click listener to change the color of the square
        holder.colorableSquare.setOnClickListener(v -> {
            // Change to a random color or a fixed color
            int newColor = Color.BLACK; // Example: Change to black
            item.setColor(newColor);
            holder.colorableSquare.setBackgroundColor(newColor);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, quantityDisplay;
        View colorableSquare;
        Button incrementButton, decrementButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            quantityDisplay = itemView.findViewById(R.id.quantity_display);
            colorableSquare = itemView.findViewById(R.id.colorable_square);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
        }
    }
}

/*public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create list of items
        itemList = new ArrayList<>();
        itemList.add(new Item("Item 1", 0, Color.GRAY));
        itemList.add(new Item("Item 2", 0, Color.GRAY));

        // Set up adapter
        adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }
}
*/
