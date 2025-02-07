package com.example.farfromhome.menu;

import android.Manifest;

import com.example.farfromhome.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.farfromhome.HomeActivity;

public class HorizontalMenuFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;


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
            shopImageView.setClickable(true);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            shopImageView.setOnClickListener(v -> checkLocationPermissionAndOpenMaps());
        } else {
            shopImageView.setVisibility(View.GONE);
            shopImageView.setClickable(false);
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


    private FusedLocationProviderClient fusedLocationClient;
    private void checkLocationPermissionAndOpenMaps() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndOpenMaps();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);}

    private void fetchLocationAndOpenMaps() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                openGoogleMapsWithLocation(location.getLatitude(), location.getLongitude());
            } else {
                openGoogleMapsWithoutLocation();
            }
        }).addOnFailureListener(e -> openGoogleMapsWithoutLocation());
    }

    private void openGoogleMapsWithLocation(double latitude, double longitude) {
        String geoUri = "geo:" + latitude + "," + longitude + "?q=supermercato";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            openGoogleMapsWithoutLocation();
        }
    }

    private void openGoogleMapsWithoutLocation() {
        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=supermercato";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndOpenMaps();
            } else {
                HomeActivity.showCustomToast(requireContext(),"Permesso negato! Apertura mappe senza posizione.");
                openGoogleMapsWithoutLocation();
            }
        }
    }

}
