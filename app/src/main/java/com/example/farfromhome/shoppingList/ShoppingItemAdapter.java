package com.example.farfromhome.shoppingList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder> {

    private List<Item> items;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private DatabaseHelper dbHelper;

    public ShoppingItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        this.dbHelper = new DatabaseHelper(context);
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
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
                holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            } else {
                showConfirmDialog(item, position);
            }
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

    private void showConfirmDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rimuovere dalla lista?");
        builder.setMessage("La quantità è 0. Vuoi rimuovere l'elemento dalla lista della spesa?");

        builder.setPositiveButton("Sì", (dialog, which) -> {
            dbHelper.removeShoppingListItem(item.getName());
            items.remove(position);
            notifyItemRemoved(position);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            item.setQuantity(1);
            notifyItemChanged(position);
        });

        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ShoppingItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        View incrementButton, decrementButton, colorChangeView;

        public ShoppingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            colorChangeView = itemView.findViewById(R.id.colorChangeView);
        }
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}
