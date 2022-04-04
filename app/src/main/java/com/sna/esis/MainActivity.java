package com.sna.esis;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.sna.esis.data.FireBaseDataSendOrReceive;
import com.sna.esis.view_models.MainActivity_ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private final Handler handler = new Handler();
    LocationEngine locationEngine;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private TextView currentSpeed_txt;
    private LocationComponent locationComponent;
    private float zoomLevel = 7f;
    private ArrayList<LocationSpeedLimitModels> limitModelsList = new ArrayList<>();
    private HashMap<Marker, LocationSpeedLimitModels> markers_hashMap = new HashMap<>();
    private MainActivity_ViewModel viewModel;
    private ImageView mainMenu, addLocation;
    private PopupMenu popupMenu;
    private FireBaseDataSendOrReceive fireBaseDataSendOrReceive;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        initView();
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
//        getDataFromFireBase();
        mapBoxIsLoaded();

    }

    private void mapBoxIsLoaded(){
        viewModelObservers();
        fireBaseDataSendOrReceive = new FireBaseDataSendOrReceive(MainActivity.this, viewModel);
        fireBaseDataSendOrReceive.getRootNamesData("Path");
        AllClicksHandle();

    }

    private void initView() {
        currentSpeed_txt = findViewById(R.id.currentSpeed_txt);
        mapView = findViewById(R.id.mapView);
        mainMenu = findViewById(R.id.menu_ic_main);
        addLocation = findViewById(R.id.addLocationIc_main);
        viewModel = new ViewModelProvider(this).get(MainActivity_ViewModel.class);
        popupMenu = new PopupMenu(MainActivity.this, mainMenu);

    }

    private void AllClicksHandle() {
        mainMenu.setOnClickListener(this::mainMenuIc_click);
        popupMenu.setOnMenuItemClickListener(this::popUpMenuClick);
        addLocation.setOnClickListener(this::addLocationClick);
    }

    private void addLocationClick( View view ) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_location_dialog);
        Spinner spinner = dialog.findViewById(R.id.spinner_addLocationDialog);
        HashMap<Integer,String> hashMap =  viewModel.parentNode_hashMap.getValue();
        String[] nameList = new String[hashMap.size()];
        for(int i=0; i<hashMap.size(); i++){
            nameList[i] = hashMap.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(R.layout.spinner_custom_textview);
        spinner.setAdapter(adapter);

        dialog.show();
    }

    private void mainMenuIc_click( View view ) {
        popupMenu.show();
    }

    private void viewModelObservers() {
        viewModel.parentNode_hashMap.observe(this, value -> {
            setPopUpMenu(value);
        });
        viewModel.locationList.observe(this, value -> {
            setMakerOnMap(value);
        });

    }

    private void setPopUpMenu( HashMap<Integer, String> hashMap ) {
        popupMenu.getMenu().clear();
        for (int i = 0; i < hashMap.size(); i++) {
            String rootName = hashMap.get(i);
            popupMenu.getMenu().add(rootName);
        }
    }

    private boolean popUpMenuClick( MenuItem menuItem ) {
        int key = 0;
//        getDataFromFireBase();
        HashMap<Integer, String> value = viewModel.parentNode_hashMap.getValue();
        String data = menuItem.getTitle().toString();
        for (int i = 0; i < value.size(); i++) {
            if (data.equals(value.get(i))) {
                fireBaseDataSendOrReceive.getLowerNodeData(i, data);
                return false;
            }
        }
        return false;
    }

    private void getDataFromFireBase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Path");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        System.out.println(dataSnapshot);
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            System.out.println(dataSnapshot1.getKey().toString());
                            if (dataSnapshot1.child("Name").getValue() != null) {
                                String rootNodeName = dataSnapshot1.child("Name").getValue().toString();
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    if (dataSnapshot2.child("Name").getValue() != null && dataSnapshot2.child("SpeedLimit").getValue() != null) {
                                        String locationName = dataSnapshot2.child("Name").getValue().toString();
                                        String speedLimit = dataSnapshot2.child("SpeedLimit").getValue().toString();
                                        String latitude = dataSnapshot2.child("Location").child("Latitude").getValue().toString();
                                        String longitude = dataSnapshot2.child("Location").child("Longitude").getValue().toString();
                                        LocationSpeedLimitModels models = new LocationSpeedLimitModels(rootNodeName, locationName, speedLimit, latitude, longitude);
                                        limitModelsList.add(models);
                                    }
                                }
                            }
                        }
                        setMakerOnMap(limitModelsList);
                    }
                } catch (NullPointerException databaseException) {
                    Toast.makeText(MainActivity.this, "FireBase Data is Not Correct Format", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError error ) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMakerOnMap( ArrayList<LocationSpeedLimitModels> limitModelsList ) {

        if(markers_hashMap.size() > 0){
            mapboxMap.removeAnnotations();
        }
        for (int i = 0; i < limitModelsList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.setPosition(new LatLng(Double.valueOf(limitModelsList.get(i).getLatitude()), Double.valueOf(limitModelsList.get(i).getLongitude())));
            Marker marker = new Marker(markerOptions);
            markerOptions.title(limitModelsList.get(i).getNameLocation());
            markerOptions.setSnippet("Speed Limit = " + limitModelsList.get(i).getSpeedLimit());
            markers_hashMap.put(marker, limitModelsList.get(i));
            mapboxMap.addMarker(markerOptions);
        }
        if(mapboxMap != null)
        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick( @NonNull Marker marker ) {
                for (LocationSpeedLimitModels models : limitModelsList) {
                    if (Double.valueOf(models.getLatitude()) == marker.getPosition().getLatitude() &&
                            Double.valueOf(models.getLongitude()) == marker.getPosition().getLongitude()) {
//                        Log.d(TAG, "onMarkerClick: " + marker.getPosition().getLongitude() + "  " + marker.getPosition().getLatitude());
//                        Log.d(TAG, "onMarkerClick: Speed  =" + models.getSpeedLimit());
                    }
                }
                return false;
            }
        });

    }

    private void setHandler() {
        new Runnable() {
            @Override
            public void run() {
                currentSpeed_txt.setText(String.valueOf((int) (locationComponent.getLastKnownLocation().getSpeed() * 3.6)));
                handler.postDelayed(this, 1000);
            }
        }.run();
    }

    @Override
    public void onMapReady( @NonNull MapboxMap mapboxMap ) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.DARK,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded( @NonNull Style style ) {
                        enableLocationComponent(style);

                    }
                });
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
        mapboxMap.getUiSettings().setScrollGesturesEnabled(true);
        mapboxMap.getUiSettings().setAllGesturesEnabled(true);

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent( @NonNull Style loadedMapStyle ) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            System.out.println(locationComponent.getLastKnownLocation().getSpeed());
            new CountDownTimer(6000, 30) {
                @Override
                public void onTick( long l ) {
                    if (l < 3800) {
                        zoomLevel += 0.1;
                        Location location = locationComponent.getLastKnownLocation();
                        setCamerpostion(location, zoomLevel);
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();
            currentSpeed_txt.setText(String.valueOf((int) (locationComponent.getLastKnownLocation().getSpeed() * 3.6)));
            setHandler();
        } else {
            permissionsManager = new PermissionsManager((PermissionsListener) MainActivity.this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void setCamerpostion( Location camerpostion, float zoomLevel ) {
        LatLng latLng = new LatLng(camerpostion.getLatitude(), camerpostion.getLongitude());
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded( List<String> permissionsToExplain ) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult( boolean granted ) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded( @NonNull Style style ) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onSaveInstanceState( @NonNull Bundle outState ) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}