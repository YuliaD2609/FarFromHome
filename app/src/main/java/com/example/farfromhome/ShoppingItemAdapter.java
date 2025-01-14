package com.example.farfromhome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.PantryItemViewHolder> {

    private final List<PantryItem> pantryItems;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ShoppingItemAdapter(Context context, List<PantryItem> pantryItems) {
        this.context = context;
        this.pantryItems = pantryItems;
    }

    @NonNull
    @Override
    public PantryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new PantryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryItemViewHolder holder, int position) {
        PantryItem pantryItem = pantryItems.get(position);

        // Assegna i dati al layout
        holder.itemName.setText(pantryItem.getName());
        holder.itemQuantity.setText(String.valueOf(pantryItem.getQuantity()));

        if (pantryItem.getImageResource() != null) {
            holder.itemImage.setImageResource(pantryItem.getImageResource());
            holder.itemImage.setVisibility(View.VISIBLE);
        } else {
            holder.itemImage.setVisibility(View.GONE);
        }

        // Listener per i pulsanti
        holder.incrementButton.setOnClickListener(v -> {
            pantryItem.incrementQuantity();
            holder.itemQuantity.setText(String.valueOf(pantryItem.getQuantity()));
        });

        holder.decrementButton.setOnClickListener(v -> {
            pantryItem.decrementQuantity();
            holder.itemQuantity.setText(String.valueOf(pantryItem.getQuantity()));
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public static class PantryItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        ImageView itemImage;
        Button incrementButton, decrementButton;

        public PantryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemImage = itemView.findViewById(R.id.itemImage);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}
