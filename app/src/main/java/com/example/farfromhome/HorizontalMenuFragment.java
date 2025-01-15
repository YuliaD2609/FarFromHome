package com.example.farfromhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import androidx.fragment.app.Fragment;

public class HorizontalMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.horizontal_menu, container, false);
        assert getArguments() != null;
        String title = getArguments().getString("TITLE", "Default Title");

        TextView titleTextView = rootView.findViewById(R.id.activityTitle); // Assicurati che l'ID del TextView nel layout sia "title"
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
}
