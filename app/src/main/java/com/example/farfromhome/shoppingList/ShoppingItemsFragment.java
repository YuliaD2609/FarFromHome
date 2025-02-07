package com.example.farfromhome.shoppingList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class ShoppingItemsFragment extends Fragment {

    private RecyclerView itemList;
    private ShoppingItemAdapter itemAdapter;
    private List<Item> items = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        itemList = rootView.findViewById(R.id.itemList);
        dbHelper = new DatabaseHelper(requireContext());

        System.out.println("preso helper");
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

    public void loadItems(String category) {
        if (category == null || category.isEmpty()) {
            items = dbHelper.getAllShoppingListItems();
        } else {
            items = dbHelper.getShoppingListItemsByCategory(category);
        }

        System.out.println(items);

        if (itemAdapter == null) {
            itemAdapter = new ShoppingItemAdapter(requireContext(), items);
            itemList.setLayoutManager(new LinearLayoutManager(requireContext()));
            itemList.setAdapter(itemAdapter);
        } else {
            itemAdapter.updateItems(items);
        }
    }

    public void updateItemList(List<Item> newItems) {
        if (itemAdapter != null) {
            itemAdapter.updateItems(newItems);
        }
    }

}
