package com.sna.esis;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.sna.esis.data.FireBaseDataSendOrReceive;
import com.sna.esis.data.GetDistanceBetweenTwoPoint;
import com.sna.esis.data.Roots_Name_ListViewAdapter;
import com.sna.esis.view_models.MainActivity_ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, Roots_Name_ListViewAdapter.lisViewItemClick {
    private final Handler handler = new Handler();
    private LocationEngine locationEngine;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private TextView currentSpeed_txt, maxSpeedLimit_txt;
    private LocationComponent locationComponent;
    private float zoomLevel = 7f;
    private ArrayList<LocationSpeedLimitModels> limitModelsList = new ArrayList<>();
    private HashMap<Marker, LocationSpeedLimitModels> markers_hashMap = new HashMap<>();
    private MainActivity_ViewModel viewModel;
    private ImageView mainMenu, addLocation, closeBtn_roots, focusOnCurrentLocation;
    private LinearLayout linearLayout_roots;
    private PopupMenu popupMenu;
    private FireBaseDataSendOrReceive fireBaseDataSendOrReceive;
    private String currentParentNodeName;
    private int currentParentNodeKey = 0;
    private GetDistanceBetweenTwoPoint getDistanceBetweenTwoPoint = new GetDistanceBetweenTwoPoint();
    private boolean startFlag = true;
    private int currentTypePosition = 4;
    private int isTimeToShowDialog = 3;
    private String prevText = "";
    private int tempSecond = 5;
    private ListView listView_roots;
    private ArrayList<String> listOfRootName = new ArrayList<>();
    private ArrayList<Drawable> listOfRootImage = new ArrayList<>();
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        initView();
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
//        ScriptGroup.Binding binding = savedInstanceState.inflate(getLayoutInflater());
//        viewAnnotationManager = mapboxMap.
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
//        getDataFromFireBase();
        mapBoxIsLoaded();

    }

    private void setMaxSpeedLimit_txt() {
        limitModelsList = viewModel.locationList.getValue();
        if (limitModelsList.size() > 0 && markers_hashMap.size() > 0) {
            int j = Integer.MAX_VALUE;
            double minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < limitModelsList.size(); i++) {
                Location location = mapboxMap.getLocationComponent().getLastKnownLocation();
                double currentDistance = getDistanceBetweenTwoPoint.haversine(location.getLatitude(), location.getLongitude(),
                        Double.parseDouble(limitModelsList.get(i).getLatitude()), Double.parseDouble(limitModelsList.get(i).getLongitude()));
                if (startFlag) {
                    if (minDistance >= currentDistance) {
                        minDistance = currentDistance;
                        j = i;
                    }
                } else {
                    if (currentDistance <= 0.14) {
                        if (minDistance >= currentDistance) {
                            minDistance = currentDistance;
                            j = i;
                            if (isTimeToShowDialog == 1)
                                isTimeToShowDialog = 2;
                        }
                    } else isTimeToShowDialog = 1;
                }
            }
            if (j != Integer.MAX_VALUE) {
                maxSpeedLimit_txt.setText(String.valueOf(limitModelsList.get(j).getSpeedLimit()));
                if (!prevText.equals(maxSpeedLimit_txt.getText().toString()) && !startFlag && !maxSpeedLimit_txt.getText().equals("0")) {
                    timeToShowDialog(limitModelsList.get(j));
                    prevText = maxSpeedLimit_txt.getText().toString();
                }
                startFlag = !(Double.parseDouble(maxSpeedLimit_txt.getText().toString()) > 0);
//                if (isTimeToShowDialog == 2) {
//                    isTimeToShowDialog = 3;
//                    timeToShowDialog(limitModelsList.get(j));
//                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void timeToShowDialog( LocationSpeedLimitModels locationSpeedLimitModels ) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.showautomet_location_dialog);
        TextView locationName_txt = dialog.findViewById(R.id.locationName_showAutomateD),
                speedLimit_txt = dialog.findViewById(R.id.speedLimitName_showAutomateD),
                countDown_text = dialog.findViewById(R.id.countDownSecond_txt_showAutomateD);
        Button closeBtn = dialog.findViewById(R.id.closeBtn_showAutomateD);
        ImageView locationBox_imv = dialog.findViewById(R.id.locationBox_showAutomateD);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String text;
        switch (locationSpeedLimitModels.getType()) {
            case "1":
                locationBox_imv.setImageResource(R.drawable.ic_bigsingle);
                text = "a signal is coming\nname " + locationSpeedLimitModels.getNameLocation();
                break;

            case "3":
                locationBox_imv.setImageResource(R.drawable.ic_bigdanger);
                text = "this is a temporary caution\nname " + locationSpeedLimitModels.getNameLocation();
                break;

            default:
                locationBox_imv.setImageResource(R.drawable.ic_biglocation);
                text = "You are at " + locationSpeedLimitModels.getNameLocation();
        }
        locationName_txt.setText(text);
        playTextToSpeech(text + " and your new speed limits is " + locationSpeedLimitModels.getSpeedLimit() + " Kilometer per hour");
        speedLimit_txt.setText(locationSpeedLimitModels.getSpeedLimit() + " kmph");
        countDown_text.setText("it will automatically get close\nin - 5 second");
        tempSecond = 5;
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick( long l ) {
                tempSecond--;
                countDown_text.setText("it will automatically get close\nin - " + tempSecond + " second");
            }

            @Override
            public void onFinish() {
                dialog.cancel();
            }
        }.start();

        closeBtn.setOnClickListener(view -> {
            countDownTimer.cancel();
            dialog.cancel();
        });

    }

    private void playTextToSpeech( String Sound ) {
        textToSpeech.setLanguage(Locale.ENGLISH);
        textToSpeech.setSpeechRate(1.0f);
        textToSpeech.speak(Sound, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void mapBoxIsLoaded() {
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
        maxSpeedLimit_txt = findViewById(R.id.maxSpeedLimit_txt);
        focusOnCurrentLocation = findViewById(R.id.focusCurrentLocationIC_main);
        viewModel = new ViewModelProvider(this).get(MainActivity_ViewModel.class);
        popupMenu = new PopupMenu(MainActivity.this, mainMenu);
        closeBtn_roots = findViewById(R.id.closeIc_tempLinear_main);
        linearLayout_roots = findViewById(R.id.tempLinearLayout_main);
        linearLayout_roots.setVisibility(View.GONE);
        listView_roots = findViewById(R.id.listView_showRoots_main);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit( int i ) {
                if (i == TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void AllClicksHandle() {
        mainMenu.setOnClickListener(this::mainMenuIc_click);
        popupMenu.setOnMenuItemClickListener(this::popUpMenuClick);
        addLocation.setOnClickListener(this::addLocationClick);
        closeBtn_roots.setOnClickListener(this::closeBtn_rootsClick);
        linearLayout_roots.setOnClickListener(this::mapView_Click);
    }

    private void mapView_Click( View view ) {
        linearLayout_roots.setVisibility(View.GONE);
    }

    private void closeBtn_rootsClick( View view ) {
        linearLayout_roots.setVisibility(View.GONE);
    }

    private void addLocationClick( View view ) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_location_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Spinner spinner = dialog.findViewById(R.id.spinner_addLocationDialog);
        EditText locationNumber_edt = dialog.findViewById(R.id.locationNumberEDT_addLocationD),
                speedLimit_edt = dialog.findViewById(R.id.speedLimitEDT_addLocationD);
        Button saveBtn = dialog.findViewById(R.id.saveBtn_addLocationD),
                closeBtn = dialog.findViewById(R.id.closeBtn_addLocationD);
        ImageView signalType1_ic = dialog.findViewById(R.id.signalType1_ic_main),
                locationType2_ic = dialog.findViewById(R.id.locationType2_ic_main),
                dangerType3_ic = dialog.findViewById(R.id.dangerTyp3_ic_main);
        ArrayList<FireBaseDataModels> fireBaseDataModels = viewModel.firebaseDataModels.getValue();
        String[] nameList = new String[fireBaseDataModels.size()];
        for (int i = 0; i < nameList.length; i++) {
            nameList[i] = fireBaseDataModels.get(i).getParentRootNodeName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(currentParentNodeKey);
        dialog.show();
        Drawable blueStroke = getDrawable(R.drawable.gray_back_blue_stroke);
        Drawable blackStroke = getDrawable(R.drawable.gray_back_black_stroke);
        switch (currentTypePosition) {
            case 1:
                signalType1_ic.setBackground(blueStroke);
                break;
            case 3:
                dangerType3_ic.setBackground(blueStroke);
                break;
            default:
                locationType2_ic.setBackground(blueStroke);
                currentTypePosition = 2;
        }

        signalType1_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                currentTypePosition = 1;
                signalType1_ic.setBackground(blueStroke);
                dangerType3_ic.setBackground(blackStroke);
                locationType2_ic.setBackground(blackStroke);
            }
        });
        locationType2_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                currentTypePosition = 2;
                locationType2_ic.setBackground(blueStroke);
                signalType1_ic.setBackground(blackStroke);
                dangerType3_ic.setBackground(blackStroke);
            }
        });
        dangerType3_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                currentTypePosition = 3;
                dangerType3_ic.setBackground(blueStroke);
                signalType1_ic.setBackground(blackStroke);
                locationType2_ic.setBackground(blackStroke);
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                dialog.cancel();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if (locationNumber_edt.getText().toString().length() == 0) {
                    locationNumber_edt.setError("Please Enter Location Number");
                    return;
                }
                if (speedLimit_edt.getText().toString().length() == 0) {
                    speedLimit_edt.setError("Please Enter the Speed Limit");
                    return;
                }
                String rootName = nameList[spinner.getSelectedItemPosition()];
                for (FireBaseDataModels fireBaseDataModels1 : fireBaseDataModels) {
                    if (fireBaseDataModels1.getParentRootNodeName().endsWith(rootName)) {
                        LatLng latLng = new LatLng(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(),
                                mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude());
                        fireBaseDataSendOrReceive.sendData(fireBaseDataModels1, latLng, locationNumber_edt.getText().toString(),
                                Double.parseDouble(speedLimit_edt.getText().toString()), String.valueOf(currentTypePosition));
                    }
                }
                dialog.cancel();
                Toast.makeText(MainActivity.this, "Thanks For Adding Location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mainMenuIc_click( View view ) {
        linearLayout_roots.setVisibility(View.VISIBLE);

    }

    private void viewModelObservers() {
        viewModel.firebaseDataModels.observe(this, this::setPopUpMenu);
        viewModel.locationList.observe(this, this::setMakerOnMap);

    }

    private void setPopUpMenu( ArrayList<FireBaseDataModels> fireBaseDataModels ) {
        listOfRootName = new ArrayList<>();
        listOfRootImage = new ArrayList<>();
        for (int i = 0; i < fireBaseDataModels.size(); i++) {
            String rootName = fireBaseDataModels.get(i).getParentRootNodeName();
            listOfRootName.add(rootName);
            Drawable drawable = getDrawable(R.drawable.ic_small_location);
            listOfRootImage.add(drawable);
        }
        setDataToListView(listOfRootName, listOfRootImage);
    }

    private void setDataToListView( ArrayList<String> listOfRootName, ArrayList<Drawable> listOfRootImage ) {
        Roots_Name_ListViewAdapter adapter = new Roots_Name_ListViewAdapter(MainActivity.this, listOfRootName, listOfRootImage);
        listView_roots.setAdapter(adapter);
    }


    @SuppressLint("SetTextI18n")
    private boolean popUpMenuClick( MenuItem menuItem ) {

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

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.child("Name").getValue() != null) {
                                String rootNodeName = dataSnapshot1.child("Name").getValue().toString();
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    if (dataSnapshot2.child("Name").getValue() != null && dataSnapshot2.child("SpeedLimit").getValue() != null) {
                                        String locationName = dataSnapshot2.child("Name").getValue().toString();
                                        String speedLimit = dataSnapshot2.child("SpeedLimit").getValue().toString();
                                        String latitude = dataSnapshot2.child("Location").child("Latitude").getValue().toString();
                                        String longitude = dataSnapshot2.child("Location").child("Longitude").getValue().toString();
                                        LocationSpeedLimitModels models = new LocationSpeedLimitModels(rootNodeName, locationName, speedLimit, latitude, longitude, "1");
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
    SymbolManager symbolManager;
    ViewAnnotationManager viewAnnotationManager;
  private Style style1;
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setMakerOnMap( ArrayList<LocationSpeedLimitModels> limitModelsList ) {
//
        if (markers_hashMap.size() > 0) {
            mapboxMap.removeAnnotations();

            LongSparseArray<Symbol> listOfAnnotations = symbolManager.getAnnotations();
            List<Symbol> symbols = new ArrayList<>();
            for(int i=0; i<listOfAnnotations.size(); i++){
                symbols.add(listOfAnnotations.valueAt(i));
            }
            
            symbolManager.delete(symbols);
            mapboxMap.clear();
        }


        for (int i = 0; i < limitModelsList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.setPosition(new LatLng(Double.valueOf(limitModelsList.get(i).getLatitude()), Double.valueOf(limitModelsList.get(i).getLongitude())));
            Marker marker = new Marker(markerOptions);
            markerOptions.title(limitModelsList.get(i).getNameLocation());
            markerOptions.setSnippet("Speed Limit = " + limitModelsList.get(i).getSpeedLimit());

            String currentMarkerType = "LocationIc";

            switch (limitModelsList.get(i).getType()){
                case "1":
                    currentMarkerType = "SignalIc";
                    break;
                case "2":
                    currentMarkerType = "LocationIc";
                    break;
                case "3":
                    currentMarkerType = "DangerIc";
            }

            symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(Double.valueOf(limitModelsList.get(i).getLatitude()), Double.valueOf(limitModelsList.get(i).getLongitude())))
                    .withIconImage(currentMarkerType));

//new SymbolOptions().get
//            IconFactory mIconFactory = IconFactory.getInstance(this);
////            Drawable mIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_small_location);
//          Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_small_signal);
//            Icon icon = mIconFactory.fromBitmap(bitmap);
//            markerOptions.setIcon(icon);


            markers_hashMap.put(marker, limitModelsList.get(i));
            mapboxMap.addMarker(markerOptions);
        }

        if (mapboxMap != null)
            mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick( @NonNull Marker marker ) {
                    for (LocationSpeedLimitModels models : limitModelsList) {
                        if (Double.parseDouble(models.getLatitude()) == marker.getPosition().getLatitude() &&
                                Double.parseDouble(models.getLongitude()) == marker.getPosition().getLongitude()) {

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
                setMaxSpeedLimit_txt();
                handler.postDelayed(this, 600);
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
                        focusOnCurrentLocation.setOnClickListener(MainActivity.this::focusOnLocation);
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);
                        style.addImage("DangerIc",getResources().getDrawable(R.drawable.ic_small_danger));
                        style.addImage("LocationIc",getResources().getDrawable(R.drawable.ic_small_location));
                        style.addImage("SignalIc",getResources().getDrawable(R.drawable.ic_small_signal));
                      style1 = style;

                    }
                });


        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
        mapboxMap.getUiSettings().setScrollGesturesEnabled(true);
        mapboxMap.getUiSettings().setAllGesturesEnabled(true);

    }

    private void focusOnLocation( View view ) {
        setCamerpostion(mapboxMap.getLocationComponent().getLastKnownLocation(), 16f);
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
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
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

    @Override
    public void onClick( int position ) {
        linearLayout_roots.setVisibility(View.GONE);
//        getDataFromFireBase();
        ArrayList<FireBaseDataModels> value = viewModel.firebaseDataModels.getValue();
        String data = listOfRootName.get(position);
        for (int i = 0; i < value.size(); i++) {
            if (data.equals(value.get(i).getParentRootNodeName())) {
                startFlag = true;
                fireBaseDataSendOrReceive.getLowerNodeData(i, data);
                currentParentNodeKey = i;
                currentParentNodeName = data;
                maxSpeedLimit_txt.setText("00");
            }
        }
    }
}