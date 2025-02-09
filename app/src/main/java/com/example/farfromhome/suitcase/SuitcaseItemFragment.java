package com.example.farfromhome.suitcase;

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
import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.R;

import java.util.ArrayList;
import java.util.List;

public class SuitcaseItemFragment extends Fragment {

    private RecyclerView itemList;
    private SuitcaseItemAdapter itemAdapter;
    private DatabaseHelper dbHelper;
    private String categoryName;
    List<SuitcaseItem> items=new ArrayList<>();

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

    public void updateItemList(List<SuitcaseItem> newItems) {
        if (itemAdapter != null) {
            itemAdapter.updateItems(newItems);
        }
    }

    public void updateCategory(String newCategory) {
        categoryName = newCategory;
        loadItems(categoryName);
    }

    public void loadItems(String category) {
        if (category == null || category.isEmpty()) {
           items = dbHelper.getAllSuitcaseItems();
        } else {
            items = dbHelper.getSuitcaseItemsByCategory(category);
        }

        if (itemAdapter == null) {
            itemAdapter = new SuitcaseItemAdapter(requireContext(), items);
            itemList.setLayoutManager(new LinearLayoutManager(requireContext()));
            itemList.setAdapter(itemAdapter);
        } else {
            itemAdapter.updateItems(items);
        }
    }

    public void removeMarkedItems() {
        List<SuitcaseItem> itemsToRemove = new ArrayList<>();
        for (SuitcaseItem item : items) {
            if (item.isMarked()) {
                itemsToRemove.add(item);
                dbHelper.removeSuitcaseItem(item.getName());
            }
        }
        if(itemsToRemove.isEmpty()){
            HomeActivity.showCustomToast(requireContext(), "Non hai selezionato nessun elemento");
        }else{
            HomeActivity.showCustomToast(requireContext(), "La valigia è stata fatta!");
            items.removeAll(itemsToRemove);
            itemAdapter.updateItems(items);
        }
    }

}
