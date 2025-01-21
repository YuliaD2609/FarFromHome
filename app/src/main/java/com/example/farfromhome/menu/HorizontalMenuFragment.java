package com.example.farfromhome.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import com.example.farfromhome.HomeActivity;
import com.example.farfromhome.R;

public class HorizontalMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.horizontal_menu, container, false);
        assert getArguments() != null;
        String title = getArguments().getString("TITLE", "Default Title");
        boolean isShoppingList = getArguments().getBoolean("SHOW_CART", false);

        TextView titleTextView = rootView.findViewById(R.id.activityTitle);
        titleTextView.setText(title);

        titleTextView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                titleTextView.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = titleTextView.getWidth();
                resizeText(titleTextView, width);
                return true;
            }
        });

        rootView.findViewById(R.id.homeImage).setOnClickListener(v -> goToHome());

        ImageView shopImageView = rootView.findViewById(R.id.shopImage);
        if (isShoppingList) {
            shopImageView.setVisibility(View.VISIBLE);
        } else {
            shopImageView.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void resizeText(TextView textView, int availableWidth) {
        float textSize = 30f;
        textView.setTextSize(textSize);

        float textWidth = textView.getPaint().measureText(textView.getText().toString());
        while (textWidth > availableWidth && textSize > 12f) {
            textSize -= 2f;
            textView.setTextSize(textSize);
            textWidth = textView.getPaint().measureText(textView.getText().toString());
        }
    }

    public void goToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
