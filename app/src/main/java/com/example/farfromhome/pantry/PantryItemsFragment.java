package com.example.farfromhome.pantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farfromhome.DatabaseHelper;
import com.example.farfromhome.Item;
import com.example.farfromhome.R;

import java.util.ArrayList;
import java.util.List;

public class PantryItemsFragment extends Fragment {

    private RecyclerView itemList;
    private PantryItemAdapter itemAdapter;
    private List<Item> items = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        itemList = rootView.findViewById(R.id.itemList);
        dbHelper = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            categoryName = getArguments().getString("CATEGORY_NAME");
            loadItems(categoryName);
        }else{
            loadItems(null);
        }

        return rootView;
    }

    public void updateCategory(String newCategory) {
        categoryName = newCategory;
        loadItems(categoryName);
    }

    public void updateItemList(List<Item> newItems) {
        if (itemAdapter != null) {
            itemAdapter.updateItems(newItems);
        }
    }

    public void loadItems(String category) {
        if (category == null || category.isEmpty()) {
            items = dbHelper.getAllPantryItems();
        } else {
            items = dbHelper.getPantryItemsByCategory(category);
        }

        if (itemAdapter == null) {
            itemAdapter = new PantryItemAdapter(requireContext(), items);
            itemList.setLayoutManager(new LinearLayoutManager(requireContext()));
            itemList.setAdapter(itemAdapter);
        } else {
            itemAdapter.updateItems(items);
        }
    }
}
