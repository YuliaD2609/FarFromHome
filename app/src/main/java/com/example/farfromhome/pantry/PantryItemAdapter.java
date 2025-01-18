package com.example.farfromhome.pantry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryItemAdapter extends RecyclerView.Adapter<PantryItemAdapter.PantryItemViewHolder> {

    private List<Item> items;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private DatabaseHelper dbHelper;

    public PantryItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        dbHelper=new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PantryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pantry_item_layout, parent, false);
        return new PantryItemViewHolder(view);
    }

    public static class PantryItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemExpire;
        View incrementButton, decrementButton;

        public PantryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemExpire = itemView.findViewById(R.id.itemExpire);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull PantryItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        if (item.getExpiry() == null) {
            holder.itemExpire.setText("N/A");
            holder.itemExpire.setTextColor(ContextCompat.getColor(context, R.color.black)); // Default color
        } else {
            Date expiryDate = item.getExpiry();
            Calendar calendar = Calendar.getInstance();
            long diffInMillis = expiryDate.getTime() - calendar.getTimeInMillis();
            long daysToExpiry = diffInMillis / (1000 * 60 * 60 * 24);

            holder.itemExpire.setText(dateFormat.format(expiryDate));

            if (daysToExpiry >= 0 && daysToExpiry <= 14) {
                holder.itemExpire.setTextColor(ContextCompat.getColor(context, R.color.red)); // Rosso per scadenze ravvicinate
            } else {
                holder.itemExpire.setTextColor(ContextCompat.getColor(context, R.color.black)); // Colore predefinito per scadenze lontane
            }
        }

        holder.decrementButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
                dbHelper.updatePantryItem(item);
                holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            } else {
                showConfirmDialog(item, position);
            }
        });

        holder.incrementButton.setOnClickListener(v -> {
            item.incrementQuantity();
            dbHelper.updatePantryItem(item);
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        });
    }

    private void showConfirmDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aggiungere alla lista della spesa?");
        builder.setMessage("La quantità di "+item.getName()+ " è 0. Vuoi aggiungere questo elemento alla lista della spesa?");

        builder.setPositiveButton("Sì", (dialog, which) -> {
            dbHelper.addShoppingListItem(item);
            dbHelper.removePantryItem(item.getName());
            items.remove(position);
            notifyItemRemoved(position);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dbHelper.removePantryItem(item.getName());
            items.remove(position);
            notifyItemRemoved(position);
        });

        builder.setNeutralButton("Annulla", (dialog, which) -> {
            item.setQuantity(1);
            notifyItemChanged(position);
        });

        builder.create().show();
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
