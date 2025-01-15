package com.example.farfromhome.suitcase;

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

import com.example.farfromhome.R;

import java.util.List;

public class SuitcaseItemAdapter extends RecyclerView.Adapter<SuitcaseItemAdapter.SuitcaseItemViewHolder> {

    private final List<SuitcaseItem> items;
    private final Context context;

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
            item.decrementQuantity();
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SuitcaseItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        Button incrementButton, decrementButton;
        View colorChangeView;

        public SuitcaseItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            colorChangeView = itemView.findViewById(R.id.colorChangeView); // Inizializza
        }
    }
}
