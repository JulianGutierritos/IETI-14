package com.ieti.maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int ACCESS_LOCATION_PERMISSION_CODE = 44;
    private TextView address;
    private Intent intent;
    private LatLng ubication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.address = (TextView) findViewById(R.id.address);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        intent = getIntent();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (intent.getStringExtra(FormActivity.NAME) == null || intent.getStringExtra(FormActivity.DESCRIPTION) == null
            || intent.getStringExtra(FormActivity.LONGITUDE) == null || intent.getStringExtra(FormActivity.LATITUDE) == null){
            showMyLocation();
        }
        else{
            this.ubication = new LatLng(Double.parseDouble(intent.getStringExtra(FormActivity.LATITUDE)),
                    Double.parseDouble(intent.getStringExtra(FormActivity.LONGITUDE)));
            googleMap.addMarker(new MarkerOptions().position(this.ubication).title(intent.getStringExtra(FormActivity.NAME)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.ubication, 10));
        }
    }

    @SuppressWarnings("MissingPermission")
    public void showMyLocation() {
        if (googleMap != null) {
            String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION};
            if (hasPermissions(this, permissions)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                this.fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    addMarkerAndZoom(location, "My Location", 15);

                                }
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(this, permissions, ACCESS_LOCATION_PERMISSION_CODE);
            }
        }
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public void addMarkerAndZoom(Location location, String title, int zoom) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(myLocation).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == -1) {
                return;
            }
        }
        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION_CODE:
                showMyLocation();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onFindAddressClicked(View view) {
        startFetchAddressIntentService();
    }

    public void startFetchAddressIntentService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            AddressResultReceiver addressResultReceiver = new AddressResultReceiver(new Handler());
                            addressResultReceiver.setAddressResultListener(new AddressResultListener() {
                                @Override
                                public void onAddressFound(final String address) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MapsActivity.this.address.setText(address);
                                            MapsActivity.this.address.setVisibility(View.VISIBLE);
                                        }
                                    });


                                }
                            });
                            Intent intent = new Intent(MapsActivity.this, FetchAddressIntentService.class);
                            intent.putExtra(FetchAddressIntentService.RECEIVER, addressResultReceiver);
                            intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location);
                            startService(intent);
                        }
                    }
                });
    }

    public void onNewLocationClicked(View view){
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

}