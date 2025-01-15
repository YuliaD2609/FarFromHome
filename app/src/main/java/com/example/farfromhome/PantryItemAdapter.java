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

public class PantryItemAdapter extends RecyclerView.Adapter<PantryItemAdapter.PantryItemViewHolder> {

    private final List<Item> items;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public PantryItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PantryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pantry_item_layout, parent, false);
        return new PantryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.itemExpire.setText(dateFormat.format(item.getExpiry()));

        holder.incrementButton.setOnClickListener(v -> {
            item.incrementQuantity();
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        });

        holder.decrementButton.setOnClickListener(v -> {
            item.decrementQuantity();
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class PantryItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemExpire;
        ImageView itemImage;
        Button incrementButton, decrementButton;

        public PantryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemExpire = itemView.findViewById(R.id.itemExpire);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}
