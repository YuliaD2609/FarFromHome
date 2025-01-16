package com.example.farfromhome.suitcase;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.util.List;

public class SuitcaseItemAdapter extends RecyclerView.Adapter<SuitcaseItemAdapter.SuitcaseItemViewHolder> {

    private List<SuitcaseItem> items;
    private final Context context;
    private DatabaseHelper dbHelper;

    public SuitcaseItemAdapter(Context context, List<SuitcaseItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SuitcaseItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_item_layout, parent, false);
        return new SuitcaseItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuitcaseItemViewHolder holder, int position) {
        SuitcaseItem item = items.get(position);

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

            if (currentColor == context.getResources().getColor(R.color.black)) {
                holder.colorChangeView.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.itemName.setPaintFlags(holder.itemName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                holder.incrementButton.setVisibility(View.VISIBLE);
                holder.decrementButton.setVisibility(View.VISIBLE);
            } else {
                holder.colorChangeView.setBackgroundColor(context.getResources().getColor(R.color.black));
                holder.itemName.setPaintFlags(holder.itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.incrementButton.setVisibility(View.GONE);
                holder.decrementButton.setVisibility(View.GONE);
            }
        });

        dbHelper=new DatabaseHelper(context);
    }

    private void showConfirmDialog(SuitcaseItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aggiungere alla lista della spesa?");
        builder.setMessage("La quantità è 0. Vuoi aggiungere questo elemento alla lista della spesa?");

        builder.setPositiveButton("Sì", (dialog, which) -> {
            dbHelper.removeSuitcaseItem(item.getName());
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

    public static class SuitcaseItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        View colorChangeView, incrementButton, decrementButton;

        public SuitcaseItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            colorChangeView = itemView.findViewById(R.id.colorChangeView); // Inizializza
        }
    }

    public void updateItems(List<SuitcaseItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}
