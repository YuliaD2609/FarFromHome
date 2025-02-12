package com.example.farfromhome.pantry;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.SpannableString;
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

    @Override
    public void onBindViewHolder(@NonNull PantryItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        if (item.getExpiry() == null) {
            holder.itemExpire.setText("N/A");
            holder.itemExpire.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            Date expiryDate = item.getExpiry();
            Calendar calendar = Calendar.getInstance();
            long diffInMillis = expiryDate.getTime() - calendar.getTimeInMillis();
            long daysToExpiry = diffInMillis / (1000 * 60 * 60 * 24);

            String expiryText = dateFormat.format(expiryDate);
            SpannableString underlineExpiry = new SpannableString(expiryText);
            underlineExpiry.setSpan(new android.text.style.UnderlineSpan(), 0, expiryText.length(), 0);
            holder.itemExpire.setText(underlineExpiry);

            if (daysToExpiry >= 0 && daysToExpiry <= 7) {
                holder.itemExpire.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                holder.itemExpire.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }

        holder.itemExpire.setOnClickListener(v -> showDatePicker(holder.itemExpire, item));

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

    private void showDatePicker(TextView textView, Item item) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, R.style.CustomDatePickerDialog,
                (datePicker, i, i1, i2) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(i, i1, i2);
                    Calendar today = Calendar.getInstance();

                    item.setExpiry(selectedDate.getTime());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    SpannableString underlineExpiry = new SpannableString(formattedDate);
                    underlineExpiry.setSpan(new android.text.style.UnderlineSpan(), 0, formattedDate.length(), 0);

                    long diffInMillis = item.getExpiry().getTime() - today.getTimeInMillis();
                    long daysToExpiry = diffInMillis / (1000 * 60 * 60 * 24);

                    if (daysToExpiry <= 7) {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    }

                    textView.setText(underlineExpiry);
                    dbHelper.updatePantryItem(item);
                },
                currentYear, currentMonth, currentDay
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        calendar.add(Calendar.YEAR, 15);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showConfirmDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aggiungere alla lista della spesa?");
        builder.setMessage("La quantità di "+item.getName()+ " è 0. Vuoi aggiungere questo elemento alla lista della spesa?");

        builder.setPositiveButton("Sì", (dialog, which) -> {
               if(dbHelper.doesProductShoppingListExist(item.getName())) {
                   Item itemShopping=dbHelper.getShoppingListItem(item.getName());
                   item.setQuantity(itemShopping.getQuantity()+item.getQuantity());
                   dbHelper.updateShoppingListItem(item);
               }else
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
