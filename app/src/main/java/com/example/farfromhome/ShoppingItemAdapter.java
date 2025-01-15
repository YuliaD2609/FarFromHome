package com.example.farfromhome;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder> {

    private final List<Item> items;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ShoppingItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_item_layout, parent, false);
        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        holder.incrementButton.setOnClickListener(v -> {
            item.incrementQuantity();
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        });

        holder.decrementButton.setOnClickListener(v -> {
            item.decrementQuantity();
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        });

        holder.colorChangeView.setOnClickListener(v -> {
            int currentColor = ((ColorDrawable) holder.colorChangeView.getBackground()).getColor();
            if (currentColor == context.getResources().getColor(R.color.white)) {
                holder.colorChangeView.setBackgroundColor(context.getResources().getColor(R.color.black));
            } else {
                holder.colorChangeView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ShoppingItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        ImageView itemImage;
        Button incrementButton, decrementButton;
        View colorChangeView;

        public ShoppingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemImage = itemView.findViewById(R.id.itemImage);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            colorChangeView = itemView.findViewById(R.id.colorChangeView); // Inizializza
        }
    }
}
