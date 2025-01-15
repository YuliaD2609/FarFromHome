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
        // Inflating the fragment's layout
        View rootView = inflater.inflate(R.layout.horizontal_menu, container, false);

        // Recuperare il titolo passato dal Bundle
        assert getArguments() != null;
        String title = getArguments().getString("TITLE", "Default Title");

        // Impostare il titolo nel TextView
        TextView titleTextView = rootView.findViewById(R.id.activityTitle); // Assicurati che l'ID del TextView nel layout sia "title"
        titleTextView.setText(title);

        // Ridimensionare dinamicamente il testo in base alla larghezza disponibile
        titleTextView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                titleTextView.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = titleTextView.getWidth(); // Ottieni la larghezza disponibile del TextView

                // Ridimensionare il testo in base alla larghezza
                resizeText(titleTextView, width);

                return true;
            }
        });

        return rootView;
    }

    private void resizeText(TextView textView, int availableWidth) {
        float textSize = 30f;
        textView.setTextSize(textSize);

        // Misurare la larghezza del testo
        float textWidth = textView.getPaint().measureText(textView.getText().toString());

        // Ridurre la dimensione del testo se il testo non entra nella larghezza disponibile
        while (textWidth > availableWidth && textSize > 12f) {
            textSize -= 2f; // Decrementare la dimensione del testo
            textView.setTextSize(textSize);
            textWidth = textView.getPaint().measureText(textView.getText().toString());
        }
    }
}
