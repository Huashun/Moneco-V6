package com.example.liangchenzhou.moneco;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import Adapter.AnimalNameList;
import Adapter.MapNearbyAdapter;
import CheckUserState.Checkstate;
import DataBase.DatabaseHelper;
import DecodeHugeImage.DecodeImageScale;
import Entity.AnimalDisplayItem;
import Entity.CustomLocation;
import Entity.Favorite;
import Entity.Garden;
import Entity.GardenEntry;
import Entity.Gardenlayer;
import Entity.Nestbox;
import Entity.Species;
import Entity.SpeciesGrid;
import Entity.Waterlayer;
import IdentifyScope.Point;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, AdapterView.OnItemClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowLongClickListener {

    private GoogleMap mMap;
    public static final int REQUEST_ALLOW_LOCATION = 1;
    private LocationManager locationManager;
    private String polygonId1, polygonId2;
    private DatabaseHelper dbHelper;
    private HashMap<String, Integer> listOptions;
    private HashMap<String, Polygon> listPolygonMatchOptions;
    private HashMap<String, IdentifyScope.Polygon> listPolygons;
    private PopupWindow popupWindow, popupNearby;
    private ListView listViewAnim;
    private AnimalNameList animalAdapter;
    private ArrayList<AnimalDisplayItem> nameAnimalArray;
    private ImageButton animalFilter, plantFilter, fungiFilter, climateWatchFilter;
    private int currentGridCode = 0;
    private ImageButton gridSwitch, uploadMapButton, monitor, circleNearby;
    private String currentMyGrids = "";
    private String monitorGrid = "";
    private boolean monitorState = false;
    private Location curLocation = null;
    private String speciesIdentify = "";
    private Checkstate checkstate = new Checkstate(this);
    private MapNearbyAdapter mapNearbyAdapter;


    // boolean stateAccess
    //July-----
    private EditText editText;
    private Circle bigcircle, smallcircle;
    private SharedPreferences sharedPreferences;

    private HashMap<String, Integer> polygonmap, waterpondmap;
    public HashMap<Integer, Garden> gardenlist, waterlist;
    private ArrayList<Polygon> polygonList = new ArrayList<>();
    private ArrayList<Polygon> pondlist = new ArrayList<>();
    private ViewGroup infoWindow;
    private ArrayList<Species> arrayNearbyPop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dbHelper = new DatabaseHelper(this);
        listOptions = new HashMap<>();
        listPolygonMatchOptions = new HashMap<>();
        listPolygons = new HashMap<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        gridSwitch = (ImageButton) findViewById(R.id.gridSwitch);
        gridSwitch.setOnClickListener(this);
        gridSwitch.setBackgroundColor(Color.TRANSPARENT);
        uploadMapButton = (ImageButton) findViewById(R.id.uploadMapButton);
        uploadMapButton.setOnClickListener(this);
        uploadMapButton.setBackgroundColor(Color.TRANSPARENT);
        monitor = (ImageButton) findViewById(R.id.monitor);
        monitor.setOnClickListener(this);
        monitor.setBackgroundColor(Color.TRANSPARENT);
        circleNearby = (ImageButton) findViewById(R.id.circleNearby);
        circleNearby.setOnClickListener(this);
        circleNearby.setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbHelper.createDB();

        if (!checkstate.checkLogin()) {
            uploadMapButton.setImageResource(R.drawable.ic_binoculars_grey600_24dp);
        } else {
            uploadMapButton.setImageResource(R.drawable.ic_binoculars_black_24dp);
        }
//        sharedPreferences = getApplicationContext().getSharedPreferences("currentUser", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear().commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.clear();
        uploadMapButton.setClickable(true);
        uploadMapButton.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ALLOW_LOCATION);

        } else if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
                && (locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null)) {
            mMap.setMyLocationEnabled(true);
            this.switchGrid();
            this.switchMonitor();
            this.setCircleState();
            if (mMap != null) {
                displayInfo(mMap);
            }
            displayResult();

            getRecordfromHistory();
            curLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (curLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 17));
            }
        } else if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
                && locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null) {
            mMap.setMyLocationEnabled(true);
            this.switchGrid();
            this.switchMonitor();
            this.setCircleState();
            if (mMap != null) {
                displayInfo(mMap);
            }
            displayResult();

            getRecordfromHistory();
            curLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (curLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 17));
            }
        } else if (locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)
                && locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER) != null) {
            mMap.setMyLocationEnabled(true);
            this.switchGrid();
            this.switchMonitor();
            this.setCircleState();
            if (mMap != null) {
                displayInfo(mMap);
            }
            displayResult();

            getRecordfromHistory();
            curLocation = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
            if (curLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 17));
            }
        } else {
            circleNearby.setClickable(false);
            gridSwitch.setClickable(false);
            monitor.setClickable(false);
            uploadMapButton.setClickable(false);
            uploadMapButton.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),
                    "Sorry the app can't find GPS model on this device!", Toast.LENGTH_SHORT).show();
        }


        //this.validatePolpygon();
//            if (this.getIntent().getParcelableArrayListExtra("speciesLocations") != null) {
//                ArrayList<SpeciesKingdom> custLocations = this.getIntent().getParcelableArrayListExtra("speciesLocations");
//                for (SpeciesKingdom locationSpecies : custLocations) {
//                    this.addMarkers(locationCustom);
//                }
//            }
    }

//    //add markers for locations
//    public void addMarkers(CustomLocation customLocation) {
//        LatLng latLng = new LatLng(customLocation.getCuslatitude(), customLocation.getCuslongitude());
//        mMap.addMarker(new MarkerOptions().position(latLng).title("Species"));
//    }

    //get record from history screen
    public void getRecordfromHistory() {
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("customLocation") != null) {
                CustomLocation cuslocation = getIntent().getExtras().getParcelable("customLocation");
                LatLng latLng = new LatLng(cuslocation.getCuslatitude(), cuslocation.getCuslongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Species Location"));
            }
        }
    }


    //Lawrence
    //set location listener and validate polygon
    public void validatePolpygon() {
        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
                || locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
//                if (locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null){
//                    Location curLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 16));
//
//                } else if (locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null) {
//                    Location curLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 16));
//                }

                if (gridSwitch.getTag().equals("ON")) {
                    listOptions.clear();
                    listPolygonMatchOptions.clear();
                    listPolygons.clear();

                    ArrayList<Integer> arrayIndex = new ArrayList<>();
                    ArrayList<LatLng> arrayValues = new ArrayList<>();
                    LinkedHashMap latLngHashMap = new LinkedHashMap<>();
                    latLngHashMap = dbHelper.viewGridPoints();
                    Iterator iterator = latLngHashMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        Object gridNo = entry.getKey();
                        arrayIndex.add((Integer) gridNo);
                        Object pointGrid = entry.getValue();
                        arrayValues.add((LatLng) pointGrid);
                    }
                    for (int i = 0; i < arrayIndex.size(); i++) {
                        //test: mark grid points
                        //mMap.addMarker(new MarkerOptions().position(lists.get(i)).title(i + ": " + lists.get(i).latitude + ", " + lists.get(i).longitude));
                        if (i < 194) {
                            if (i != 14 && i != 29 && i != 44 && i != 59
                                    && i != 74 && i != 89 && i != 104 && i != 119
                                    && i != 134 && i != 149 && i != 164 && i != 179) {
                                PolygonOptions option = new PolygonOptions()
                                        .add(arrayValues.get(i), arrayValues.get(i + 1), arrayValues.get(i + 16), arrayValues.get(i + 15));
                                Polygon polygon = mMap.addPolygon(option);
                                polygon.setClickable(true);
                                polygon.setStrokeWidth(5);
                                polygon.setStrokeColor(Color.rgb(117, 117, 117));

                                int code = dbHelper.matchGridNo(arrayIndex.get(i), arrayIndex.get(i + 1),
                                        arrayIndex.get(i + 15), arrayIndex.get(i + 16));
                                //use id match polygon
                                listOptions.put(polygon.getId(), code);
                                listPolygonMatchOptions.put(polygon.getId(), polygon);
                                //add polygon of IdentifyPolygon Type to a hashmap
                                IdentifyScope.Polygon polygonIdent = IdentifyScope.Polygon.Builder()
                                        .addVertex(new Point(arrayValues.get(i).latitude, arrayValues.get(i).longitude))
                                        .addVertex(new Point(arrayValues.get(i + 1).latitude, arrayValues.get(i + 1).longitude))
                                        .addVertex(new Point(arrayValues.get(i + 16).latitude, arrayValues.get(i + 16).longitude))
                                        .addVertex(new Point(arrayValues.get(i + 15).latitude, arrayValues.get(i + 15).longitude))
                                        .build();
                                listPolygons.put(polygon.getId(), polygonIdent);
                            }
                        }
                    }

                    mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                        @Override
                        public void onPolygonClick(final Polygon polygon) {
                            if (!polygon.getId().equals(currentMyGrids)) {
                                polygon.setFillColor(Color.argb(120, 123, 123, 123));
                            }
                            currentGridCode = listOptions.get(polygon.getId());
                            initializePopupWindow(polygon);
                        }

                    });

                }


                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (gridSwitch.getTag().equals("ON") && listPolygons != null && listOptions != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                            checkCurrentGrid(new LatLng(location.getLatitude(), location.getLongitude()), listPolygons);

                            if (monitorState) {
                                Point currentPoint = new Point(location.getLatitude(), location.getLongitude());
                                if (listPolygons.get(monitorGrid) != null) {
                                    IdentifyScope.Polygon curPolygon = listPolygons.get(monitorGrid);
                                    if (!curPolygon.contains(currentPoint)) {
                                        Toast.makeText(getApplicationContext(),
                                                "Notice, you are leaving current area!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Sorry, You are not in the Clayton campus of Monash University!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (circleNearby.getTag().equals("ON")) {
                            mMap.clear();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
                            drawCircle(location);
                            markNearby(location);
                        }

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
                        && (locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null)) {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3000, 8, locationListener);
                } else if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
                        && (locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null)) {
                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 3000, 8, locationListener);
                } else if (locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)
                        && (locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER) != null)) {
                    locationManager.requestLocationUpdates(locationManager.PASSIVE_PROVIDER, 3000, 8, locationListener);
                }
            }
        }
    }


    //optimize???
    //check the concrete grid that user is in
    public void checkCurrentGrid(LatLng curLocation, HashMap<String, IdentifyScope.Polygon> lists) {
        Object gridNo = "";
        Object pointGrid = new HashMap<>();
        HashMap polygonHashMap = new HashMap<>();
        Point point = new Point(curLocation.latitude, curLocation.longitude);
        polygonHashMap = lists;
        Iterator iterator = polygonHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            gridNo = entry.getKey();
            pointGrid = entry.getValue();
            IdentifyScope.Polygon indetifyPolygon = (IdentifyScope.Polygon) pointGrid;
            if (indetifyPolygon.contains(point)) {
                listPolygonMatchOptions.get(gridNo).setFillColor(Color.argb(110, 204, 230, 255));
                currentMyGrids = (String) gridNo;
            } else {
                listPolygonMatchOptions.get(gridNo).setFillColor(Color.TRANSPARENT);
            }
        }
    }

    //Lawrence
    //get the result of request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ALLOW_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Allow.", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
                            || locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
                            || locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)) {
                        if (locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null
                                || locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null
                                || locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER) != null) {
                            mMap.setMyLocationEnabled(true);
                            this.switchGrid();
                            this.switchMonitor();
                            this.setCircleState();

                            if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
                                    && locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null) {
                                curLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            } else if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
                                    && locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null) {
                                curLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                            } else if (locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)
                                    && (locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER) != null)) {
                                curLocation = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
                            }
                            if (curLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 17));
                            }

                        } else {
                            circleNearby.setClickable(false);
                            gridSwitch.setClickable(false);
                            monitor.setClickable(false);
                            uploadMapButton.setClickable(false);
                            uploadMapButton.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),
                                    "Please enable GPS Server of your device and reopen application!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        circleNearby.setClickable(false);
                        gridSwitch.setClickable(false);
                        monitor.setClickable(false);
                        uploadMapButton.setClickable(false);
                        uploadMapButton.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),
                                "Sorry the app can't find GPS model on this device!", Toast.LENGTH_SHORT).show();

                    }

                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestLocationPermissions();
                } else {
                    circleNearby.setClickable(false);
                    gridSwitch.setClickable(false);
                    monitor.setClickable(false);
                    uploadMapButton.setClickable(false);
                    uploadMapButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Permission Denied, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Lawrence
    //require location access permission for app
    public void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The application need a permission for using your location, do you want to try again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ALLOW_LOCATION);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            circleNearby.setClickable(false);
                            gridSwitch.setClickable(false);
                            monitor.setClickable(false);
                            uploadMapButton.setClickable(false);
                            uploadMapButton.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),
                                    "Permission Denied, you can reopen app or change permission in system setting.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }
    }

    //get popup Window
    public void initializePopupWindow(final Polygon poly) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_popup_window, (ViewGroup) findViewById(R.id.mapRelativeLayout), false);
        //view of parent layout
        View v = findViewById(R.id.mapRelativeLayout);
        //initialize popup window
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, (int) Math.floor(v.getHeight() * 0.5), true);
        //popup window position
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        animalFilter = (ImageButton) view.findViewById(R.id.animalFilter);
        plantFilter = (ImageButton) view.findViewById(R.id.plantFilter);
        fungiFilter = (ImageButton) view.findViewById(R.id.fungiFilter);
        climateWatchFilter = (ImageButton) view.findViewById(R.id.climateWatchFilter);
        animalFilter.setOnClickListener(this);
        animalFilter.setTag("ANIMALIA");
        plantFilter.setOnClickListener(this);
        plantFilter.setTag("Plantae");
        fungiFilter.setOnClickListener(this);
        fungiFilter.setTag("Fungi");
        climateWatchFilter.setOnClickListener(this);
        climateWatchFilter.setImageBitmap(DecodeImageScale.decodeSampledBitmapFromResource(getResources(), R.drawable.climate_mapbutton, 100, 100));
        nameAnimalArray = new ArrayList<>();
        listViewAnim = (ListView) view.findViewById(R.id.listViewAnimalName);
        listViewAnim.setOnItemClickListener(this);
        animalAdapter = new AnimalNameList(this, nameAnimalArray);
        listViewAnim.setAdapter(animalAdapter);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!poly.getId().equals(currentMyGrids)) {
                    poly.setFillColor(Color.TRANSPARENT);
                }
            }
        });

        animalFilter.setSelected(true);
        this.fetchSpeciesData(currentGridCode, (String) animalFilter.getTag());
    }

    //get climateWatch species data in grid
    public void fetchClimateData(int code) {
        speciesIdentify = "ClimateWatch";
        nameAnimalArray.clear();
        List<SpeciesGrid> listNames = dbHelper.getClimateWatchSpecies(code);
        int i;
        if (listNames.size() != 0 && listNames != null) {
            for (i = 0; i < listNames.size(); i++) {
                AnimalDisplayItem animalItem = new AnimalDisplayItem();
                animalItem.setAnimalScientificName(listNames.get(i).getScientific());
                animalItem.setAnimalCommonName(listNames.get(i).getCommon());
                String str = "Records: " + String.valueOf(listNames.get(i).getAmount());
                animalItem.setrecordTimes(str);
                animalItem.setRecordDate(listNames.get(i).getDate());
                animalItem.setKingdom(listNames.get(i).getKingdom());
                nameAnimalArray.add(animalItem);
            }
        } else {
            AnimalDisplayItem animalItem = new AnimalDisplayItem();
            animalItem.setAnimalScientificName("No Records in this Area");
            animalItem.setrecordTimes("Records: 0");
            nameAnimalArray.add(animalItem);
        }
        animalAdapter.notifyDataSetChanged();
    }

    //get animals' scientific name list
    public void fetchSpeciesData(int code, String species) {
        speciesIdentify = species;
        nameAnimalArray.clear();
        List<SpeciesGrid> listNames = dbHelper.getScientificNames(code, species);
        int i;
        if (listNames.size() != 0 && listNames != null) {
            for (i = 0; i < listNames.size(); i++) {
                AnimalDisplayItem animalItem = new AnimalDisplayItem();
                animalItem.setAnimalScientificName(listNames.get(i).getScientific());
                animalItem.setAnimalCommonName(listNames.get(i).getCommon());
                String str = "Records: " + String.valueOf(listNames.get(i).getAmount());
                animalItem.setrecordTimes(str);
                animalItem.setRecordDate(listNames.get(i).getDate());
                nameAnimalArray.add(animalItem);
            }
        } else {
            AnimalDisplayItem animalItem = new AnimalDisplayItem();
            animalItem.setAnimalScientificName("No Records in this Area");
            animalItem.setrecordTimes("Records: 0");
            nameAnimalArray.add(animalItem);
        }
        animalAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.animalFilter) {
            this.fetchSpeciesData(currentGridCode, (String) animalFilter.getTag());
        } else if (v.getId() == R.id.plantFilter) {
            this.fetchSpeciesData(currentGridCode, (String) plantFilter.getTag());
        } else if (v.getId() == R.id.fungiFilter) {
            this.fetchSpeciesData(currentGridCode, (String) fungiFilter.getTag());
        } else if (v.getId() == R.id.climateWatchFilter) {
            this.fetchClimateData(currentGridCode);
        } else if (v.getId() == R.id.gridSwitch) {
            if (popupNearby != null) {
                popupNearby.dismiss();
            }
            this.switchGrid();
            this.switchMonitor();
        } else if (v.getId() == R.id.monitor) {
            this.monitorCheck();
        } else if (v.getId() == R.id.uploadMapButton) {
            Checkstate checkstate = new Checkstate(this);
            if (!checkstate.checkLogin()) {
                Toast.makeText(this, "Please login firstly", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MapsActivity.this, UploadObservation.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.circleNearby) {
            this.setCircleState();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!nameAnimalArray.get(position).getAnimalScientificName().equals("No Records in this Area")) {
            if (!speciesIdentify.equals("ClimateWatch")) {
                String scientificName = nameAnimalArray.get(position).getAnimalScientificName();
                Intent intent = new Intent(MapsActivity.this, Media.class);
                intent.putExtra("scientificSpecies", scientificName);
                switch (speciesIdentify) {
                    case "ANIMALIA":
                        intent.putExtra("species", "animal");
                        break;
                    case "Plantae":
                        intent.putExtra("species", "plant");
                        break;
                    case "Fungi":
                        intent.putExtra("species", "fungi");
                        break;
                }
                startActivity(intent);
            } else {
                String scientificName = nameAnimalArray.get(position).getAnimalScientificName();
                Intent intent = new Intent(MapsActivity.this, Media.class);
                intent.putExtra("scientificSpecies", scientificName);
                String identifySpeciesName = nameAnimalArray.get(position).getKingdom();
                switch (identifySpeciesName) {
                    case "ANIMALIA":
                        intent.putExtra("species", "animal");
                        break;
                    case "Plantae":
                        intent.putExtra("species", "plant");
                        break;
                    case "Fungi":
                        intent.putExtra("species", "fungi");
                        break;
                }
                startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, please choose a species record firstly", Toast.LENGTH_SHORT).show();

        }
    }

    //grid on or grid off method
    public void switchGrid() {
        if (gridSwitch.getTag() != null) {
            if (!(gridSwitch.getTag()).equals("OFF")) {
                mMap.clear();
                //1. make map clear or remove polygon
                //2. just set invisible of grid attribute
                gridSwitch.setImageResource(R.drawable.ic_grid_off_grey600_24dp);
                gridSwitch.setTag("OFF");
                this.validatePolpygon();
            } else if (!(gridSwitch.getTag()).equals("ON")) {
                mMap.clear();
                circleNearby.setTag("OFF");
                circleNearby.setImageResource(R.drawable.ic_radar_grey600_24dp);
                gridSwitch.setImageResource(R.drawable.ic_grid_black_24dp);
                gridSwitch.setTag("ON");
                this.validatePolpygon();

            }
        } else if (gridSwitch.getTag() == null) {
            gridSwitch.setImageResource(R.drawable.ic_grid_off_grey600_24dp);
            gridSwitch.setTag("OFF");
            this.validatePolpygon();
        }
    }

    //monitor visibility
    public void switchMonitor() {
        if (gridSwitch.getTag().equals("ON")) {
            monitor.setVisibility(View.VISIBLE);
            monitor.setClickable(true);
            monitor.setImageResource(R.drawable.ic_bell_off_grey600_24dp);
            monitor.setTag("OFF");
            monitorState = false;
        } else {
            monitor.setVisibility(View.GONE);
            monitor.setClickable(false);
            monitorState = false;
        }
    }

    //monitor on or monitor off method
    public void monitorCheck() {
        if (!(monitor.getTag()).equals("OFF")) {
            //1. make map clear or remove polygon
            //2. just set invisible of grid attribute
            monitor.setImageResource(R.drawable.ic_bell_off_grey600_24dp);
            monitor.setTag("OFF");
            monitorState = false;
        } else if (!(monitor.getTag()).equals("ON")) {
            monitor.setImageResource(R.drawable.ic_bell_ring_black_24dp);
            monitor.setTag("ON");
            monitorCurrentArea();
            monitorState = true;
            monitorGrid = currentMyGrids;
        }
    }

    //monitor enabled and will informing if user leave current grid
    public void monitorCurrentArea() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
        dialog.setTitle("Notification")
                .setMessage("You will be notified when you leave current monitoring area")
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
        monitorState = true;
    }

    //July------------
    //setCircle State
    public void setCircleState() {
        if (circleNearby.getTag() != null) {
            if (circleNearby.getTag().equals("ON")) {
                circleNearby.setImageResource(R.drawable.ic_radar_grey600_24dp);
                circleNearby.setTag("OFF");
                if (popupNearby != null) {
                    popupNearby.dismiss();
                }
                mMap.clear();
            } else if (circleNearby.getTag().equals("OFF")) {
                mMap.clear();
                gridSwitch.setTag("OFF");
                gridSwitch.setImageResource(R.drawable.ic_grid_off_grey600_24dp);
                switchMonitor();
                circleNearby.setImageResource(R.drawable.ic_radar_black_24dp);
                circleNearby.setTag("ON");

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                        if (locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null) {
                            curLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 18));
                        }
                    } else if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
                        if (locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null) {
                            curLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 18));
                        }
                    } else if (locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)) {
                        if (locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER) != null) {
                            curLocation = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 18));
                        }
                    }
                }
                //must be initialized before markNearby method
                initialPopupNearby();
                drawCircle(curLocation);
                markNearby(curLocation);
            }
        } else {
            if (popupNearby != null) {
                popupNearby.dismiss();
            }
            circleNearby.setImageResource(R.drawable.ic_radar_grey600_24dp);
            circleNearby.setTag("OFF");
            //mMap.clear();
        }

    }


    @Override
    public void onInfoWindowLongClick(final Marker marker) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to save this entity into my favorite?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isLogin()) {
                            saveTofavorite(marker);
                        } else {
                            toastShow("Please login firstly");

                        }
                        //ActivityCompat.requestPermissions(UploadObservation.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    //created method: polygon listener
    @Override
    public void onMapClick(LatLng point) {
        boolean contains = false;
//Polygon clickpg = null;
        for (Polygon p : polygonList) {
            contains = PolyUtil.containsLocation(point, p.getPoints(), false);
            if (contains) {
                p.setStrokeColor(Color.WHITE);
                int key = polygonmap.get(p.getId());
                String name = gardenlist.get(key).getTitle();
                Intent intent = new Intent(MapsActivity.this, GardeninfoActivity.class);
                intent.putExtra("gardenname", name);
                startActivity(intent);
                if (!name.equals("")) {
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        for (Polygon p : pondlist) {
            contains = PolyUtil.containsLocation(point, p.getPoints(), false);
            if (contains) {
                p.setStrokeColor(Color.WHITE);
                int key = waterpondmap.get(p.getId());
                String name = waterlist.get(key).getTitle();
                Intent intent = new Intent(MapsActivity.this, GardeninfoActivity.class);
                intent.putExtra("watername", name);
                startActivity(intent);
                if (!name.equals("")) {
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    public void home(View view) {
        mMap.clear();
    }

    public void search(View view) {
        Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    public void searchBox(View view) {
        editText = (EditText) findViewById(R.id.editView);
        String searchBox = editText.getText().toString();
        Intent intent = new Intent(MapsActivity.this, SearchBoxActivity.class);
        intent.putExtra("searchBox", searchBox);
        startActivity(intent);
    }

    public String getFamily(String scientific) {
        String faimly = null;
        if (scientific == null) {
            return null;
        } else {
            dbHelper = new DatabaseHelper(getApplicationContext());
            if (dbHelper.isNestBox(scientific)) {
                return null;
            } else {
                faimly = dbHelper.getFamily(scientific, "plant");
                if (faimly == null) {
                    faimly = dbHelper.getFamily(scientific, "animal");
                    if (faimly == null) {
                        faimly = dbHelper.getFamily(scientific, "fungi");
                        if (faimly == null) {
                            return null;
                        } else {

                            return faimly;
                        }
                    } else {

                        return faimly;
                    }
                } else {

                    return faimly;
                }
            }
        }
    }

    public Boolean isLogin() {
        return checkstate.checkLogin();
    }

    //initial nearby map popup window
    public void initialPopupNearby() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_map_nearby, (ViewGroup) findViewById(R.id.mapRelativeLayout), false);
        View parent = findViewById(R.id.mapRelativeLayout);
        popupNearby = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, (int) Math.floor(parent.getHeight() * 0.25), false);
        popupNearby.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        ListView listViewMapNearby = (ListView) view.findViewById(R.id.listViewMapNearby);
        arrayNearbyPop = new ArrayList<>();
        mapNearbyAdapter = new MapNearbyAdapter(this, arrayNearbyPop);
        listViewMapNearby.setAdapter(mapNearbyAdapter);
    }

    public void markNearby(Location location) {
        if (arrayNearbyPop != null) {
            arrayNearbyPop.clear();
        }
        if (circleNearby.getTag().equals("ON")) {
            dbHelper = new DatabaseHelper(this);
            ArrayList<Species> plants = dbHelper.readSpecies("plant");
            MarkerOptions nearplant = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nearplant));
            MarkerOptions nearanimal = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nearanimal));
            MarkerOptions nearfungi = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nearfungi));
            ArrayList<Species> nearp = dbHelper.getNear(location.getLatitude(), location.getLongitude(), plants, 50);
            for (int i = 0; i < nearp.size(); i++) {
                mMap.addMarker(nearplant.position(new LatLng(nearp.get(i).getLat(), nearp.get(i).getLng()))
                        .title(nearp.get(i).getCommon())
                        .snippet(nearp.get(i).getScientific()));
            }
            //remove duplicate
            removeDuplicateRecord(nearp);
            arrayNearbyPop.addAll(nearp);
            ArrayList<Species> animals = dbHelper.readSpecies("animal");
            ArrayList<Species> neara = dbHelper.getNear(location.getLatitude(), location.getLongitude(), animals, 50);
            for (int i = 0; i < neara.size(); i++) {
                mMap.addMarker(nearanimal.position(new LatLng(neara.get(i).getLat(), neara.get(i).getLng()))
                        .title(neara.get(i).getCommon())
                        .snippet(neara.get(i).getScientific()));
            }
            //remove duplicate
            removeDuplicateRecord(neara);
            arrayNearbyPop.addAll(neara);
            ArrayList<Species> fungis = dbHelper.readSpecies("fungi");
            ArrayList<Species> nearg = dbHelper.getNear(location.getLatitude(), location.getLongitude(), fungis, 50);
            for (int i = 0; i < nearg.size(); i++) {
                mMap.addMarker(nearfungi.position(new LatLng(nearg.get(i).getLat(), nearg.get(i).getLng()))
                        .title(nearg.get(i).getCommon())
                        .snippet(nearg.get(i).getScientific()));

            }
            //remove duplicate
            removeDuplicateRecord(nearg);
            arrayNearbyPop.addAll(nearg);
            if (mapNearbyAdapter != null) {
                mapNearbyAdapter.notifyDataSetChanged();
            }
        }
    }

    public void drawCircle(Location location) {
        if (circleNearby.getTag().equals("ON")) {
            bigcircle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .strokeColor(0)
                    .radius(50)
                    .fillColor(getResources().getColor(R.color.bigcircleBlue)));
            smallcircle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .strokeColor(0)
                    .radius(10)
                    .fillColor(getResources().getColor(R.color.smallcircleBlue)));
        }
    }


    public void displayResult() {

        dbHelper = new DatabaseHelper(getApplicationContext());
        Intent intent = getIntent();
        String tablename = intent.getStringExtra("kingdom");
        MarkerOptions plantmarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.plant_marker));
        MarkerOptions animalmarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.animal_marker));
        MarkerOptions fungimarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.fungi_marker));
        String keyword1 = intent.getStringExtra("displayCom");
        if (keyword1 != null) {
            mMap.clear();
            mMap.setOnInfoWindowLongClickListener(this);
            final ArrayList<Species> displayCommon = dbHelper.displayByCommon(keyword1, tablename);
            switch (tablename) {
                case "plant":
                    for (int i = 0; i < displayCommon.size(); i++) {
                        LatLng location = new LatLng(displayCommon.get(i).getLat(), displayCommon.get(i).getLng());
                        mMap.addMarker(plantmarker.position(location).title(displayCommon.get(i).getCommon())
                                .snippet(displayCommon.get(i).getScientific()));

                    }
                    break;
                case "animal":
                    for (int i = 0; i < displayCommon.size(); i++) {
                        LatLng location = new LatLng(displayCommon.get(i).getLat(), displayCommon.get(i).getLng());
                        mMap.addMarker(animalmarker.position(location).title(displayCommon.get(i).getCommon())
                                .snippet(displayCommon.get(i).getScientific()));

                    }
                    break;
                case "fungi":
                    for (int i = 0; i < displayCommon.size(); i++) {
                        LatLng location = new LatLng(displayCommon.get(i).getLat(), displayCommon.get(i).getLng());
                        mMap.addMarker(fungimarker.position(location).title(displayCommon.get(i).getCommon())
                                .snippet(displayCommon.get(i).getScientific()));
                    }
                    break;
            }
        } else {
            String keyword2 = intent.getStringExtra("displaySci");
            if (keyword2 != null) {
                mMap.clear();
                mMap.setOnInfoWindowLongClickListener(this);
                ArrayList<Species> displaySci = dbHelper.displayByScien(keyword2, tablename);
                switch (tablename) {
                    case "plant":
                        for (int i = 0; i < displaySci.size(); i++) {
                            LatLng location = new LatLng(displaySci.get(i).getLat(), displaySci.get(i).getLng());
                            mMap.addMarker(plantmarker.position(location).title(displaySci.get(i).getCommon())
                                    .snippet(displaySci.get(i).getScientific()));
                        }
                        break;
                    case "animal":
                        for (int i = 0; i < displaySci.size(); i++) {
                            LatLng location = new LatLng(displaySci.get(i).getLat(), displaySci.get(i).getLng());
                            mMap.addMarker(animalmarker.position(location).title(displaySci.get(i).getCommon())
                                    .snippet(displaySci.get(i).getScientific()));
                        }
                        break;
                    case "fungi":
                        for (int i = 0; i < displaySci.size(); i++) {
                            LatLng location = new LatLng(displaySci.get(i).getLat(), displaySci.get(i).getLng());
                            mMap.addMarker(fungimarker.position(location).title(displaySci.get(i).getCommon())
                                    .snippet(displaySci.get(i).getScientific()));
                        }
                        break;
                }
            } else {
                mMap.setOnMapClickListener(this);
                //display all garden in map
                String keyword3 = intent.getStringExtra("gardens");
                if (keyword3 != null) {
                    mMap.clear();
                    Gardenlayer gardenlayer = new Gardenlayer();
                    Polygon polygon = null;
                    //read all garden information
                    gardenlist = new HashMap<Integer, Garden>();
                    polygonmap = new HashMap<String, Integer>();
                    gardenlist = gardenlayer.readGardens(mMap, getApplicationContext());
                    for (int i = 0; i < gardenlist.size(); i++) {
                        polygon = mMap.addPolygon(new PolygonOptions().addAll(gardenlist.get(i).getCoordinates())
                                .strokeColor(0)
                                .fillColor(getResources().getColor(R.color.garden)));
                        polygonmap.put(polygon.getId(), i);
                        polygonList.add(polygon);
                    }
                } else {
                    //display all nestbox in map
                    String keyword4 = intent.getStringExtra("nestbox");
                    if (keyword4 != null) {
                        mMap.clear();
                        ArrayList<Nestbox> nestboxes = dbHelper.getNestBox();
                        for (int i = 0; i < nestboxes.size(); i++) {
                            LatLng location = new LatLng(nestboxes.get(i).getLat(), nestboxes.get(i).getLng());
                            mMap.addMarker(new MarkerOptions().position(location)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.nestbox))
                                    .title(nestboxes.get(i).getNestboxID())
                                    .snippet(nestboxes.get(i).getLocation()));
                        }
                    } else {
                        String keyword5 = intent.getStringExtra("gardenEntry");
                        if (keyword5 != null) {
                            //display all entry for a garden
                            mMap.clear();
                            ArrayList<GardenEntry> gardenEntries = dbHelper.getEntry(keyword5);
                            if (gardenEntries == null) {
                                Toast.makeText(this, "Sorry, we do not have the entrance information of this garden", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < gardenEntries.size(); i++) {
                                    LatLng entry = new LatLng(gardenEntries.get(i).getLatitude(), gardenEntries.get(i).getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(entry)
                                            .title(gardenEntries.get(i).gardenname)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.entry)));
                                }
                                Gardenlayer.drawGardenByName(keyword5, mMap);
                            }
                        } else {
                            Bundle showinMap = intent.getBundleExtra("showMyfavo");
                            if (showinMap != null) {
                                mMap.clear();
                                mMap.setOnInfoWindowLongClickListener(this);
                                String commonname = showinMap.getString("common");
                                String scientific = showinMap.getString("scientific");
                                String kingdom = showinMap.getString("kingdom");
                                Double lat = showinMap.getDouble("lat");
                                Double lng = showinMap.getDouble("lng");
                                switch (kingdom) {
                                    case "plant":
                                        mMap.addMarker(plantmarker.position(new LatLng(lat, lng)).title(commonname)
                                                .snippet(scientific));
                                        break;
                                    case "animal":
                                        mMap.addMarker(animalmarker.position(new LatLng(lat, lng)).title(commonname).snippet(scientific));
                                        break;
                                    case "fungi":
                                        mMap.addMarker(fungimarker.position(new LatLng(lat, lng)).title(commonname).snippet(scientific));
                                        break;
                                }

                            } else {
                                String waterpond = intent.getStringExtra("WaterPonds");
                                if (waterpond != null) {
                                    mMap.clear();
                                    Waterlayer waterlayer = new Waterlayer();
                                    Polygon polygon = null;
                                    //read all garden information
                                    waterlist = new HashMap<Integer, Garden>();
                                    waterpondmap = new HashMap<>();
                                    waterlist = waterlayer.readGardens(mMap, getApplicationContext());
                                    //draw each waterpond in map
                                    for (int i = 0; i < waterlist.size(); i++) {
                                        polygon = mMap.addPolygon(new PolygonOptions().addAll(waterlist.get(i).getCoordinates())
                                                .strokeColor(0)
                                                .fillColor(getResources().getColor(R.color.smallcircleBlue)));
                                        waterpondmap.put(polygon.getId(), i);
                                        pondlist.add(polygon);
                                    }


                                } else {
                                    String dispalyall = intent.getStringExtra("Displayall");//display all entity in map green for plant
                                    //red for animal and yellow for fungi
                                    if (dispalyall != null) {
                                        mMap.clear();
                                        mMap.setOnInfoWindowLongClickListener(this);
                                        ArrayList<Species> allPlant = dbHelper.getAllEntity("plant");
                                        ArrayList<Species> allAnimal = dbHelper.getAllEntity("animal");
                                        ArrayList<Species> allFungi = dbHelper.getAllEntity("fungi");
                                        MarkerOptions nearplant = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nearplant));
                                        MarkerOptions nearanimal = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nearanimal));
                                        MarkerOptions nearfungi = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nearfungi));
                                        for (int i = 0; i < allPlant.size(); i++) {
                                            LatLng location = new LatLng(allPlant.get(i).getLat(), allPlant.get(i).getLng());
                                            mMap.addMarker(nearplant.position(location).title(allPlant.get(i).getCommon())
                                                    .snippet(allPlant.get(i).getScientific()));
                                        }
                                        for (int i = 0; i < allAnimal.size(); i++) {
                                            LatLng location = new LatLng(allAnimal.get(i).getLat(), allAnimal.get(i).getLng());
                                            mMap.addMarker(nearanimal.position(location).title(allAnimal.get(i).getCommon())
                                                    .snippet(allAnimal.get(i).getScientific()));
                                        }
                                        for (int i = 0; i < allFungi.size(); i++) {
                                            LatLng location = new LatLng(allFungi.get(i).getLat(), allFungi.get(i).getLng());
                                            mMap.addMarker(nearfungi.position(location).title(allAnimal.get(i).getCommon())
                                                    .snippet(allFungi.get(i).getScientific()));
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void displayInfo(GoogleMap map) {
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                final View infoview = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView tvCommon = (TextView) infoview.findViewById(R.id.tv_common);
                TextView tvScientific = (TextView) infoview.findViewById(R.id.tv_scientific);
                TextView tvFamily = (TextView) infoview.findViewById(R.id.tv_family);
                TextView tvDist = (TextView) infoview.findViewById(R.id.distance);
                tvCommon.setText(marker.getSnippet());
                tvScientific.setText(marker.getTitle());
                String family = getFamily(marker.getSnippet());
                if (family != null) {
                    tvFamily.setText(family.toUpperCase());
                }
                if (curLocation != null) {
                    double dist = dbHelper.getDistance(curLocation.getLatitude(), curLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude);
                    int dis = (int) dist;
                    String distance = dis + "m away";
                    tvDist.setText(distance);
                }
                return infoview;
            }

        });

    }

    public void showLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have to login first. Do you want to log in?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    public void saveTofavorite(Marker marker) {
        dbHelper = new DatabaseHelper(this.getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String currentdate = df.format(calendar.getTime());

        String common = marker.getTitle();
        String scientific = marker.getSnippet();
        String kingdom = dbHelper.getKingdomByScientific(scientific);
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;
        int userid = checkstate.handleLoggedIn().getUserId();
        Favorite myfo = new Favorite(scientific, common, kingdom, lat, lng, currentdate);
        String queryUrl = "http://moneco.monash.edu.au/insertPreference.php";
        AsyncSavetoProfile tasksave = new AsyncSavetoProfile(this, myfo, userid);
        tasksave.execute(queryUrl);

    }

    class AsyncSavetoProfile extends AsyncTask<String, Void, String> {
        private Context context;
        private Favorite myfavo;
        private int userid;

        AsyncSavetoProfile(Context context, Favorite myfavo, int userid) {
            this.context = context;
            this.myfavo = myfavo;
            this.userid = userid;
        }

        @Override
        protected String doInBackground(String... insertUrl) {
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(insertUrl[0]);
                String data = "scientificName=" + URLEncoder.encode(myfavo.getScientificname(), "UTF-8")
                        + "&commonName=" + URLEncoder.encode(myfavo.getCommonname(), "UTF-8")
                        + "&datePreference=" + URLEncoder.encode(myfavo.getDate(), "UTF-8")
                        + "&kingdom=" + URLEncoder.encode(myfavo.getKingdom(), "UTF-8")
                        + "&latitude=" + URLEncoder.encode(String.valueOf(myfavo.getLat()), "UTF-8")
                        + "&longitude=" + URLEncoder.encode(String.valueOf(myfavo.getLng()), "UTF-8")
                        + "&userId=" + URLEncoder.encode(Integer.toString(userid), "UTF-8");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
                InputStream inputStream = null;
                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();
                }
                String re = "";
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                while ((re = bufferedReader.readLine()) != null) {
                    builder.append(re);
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int states = 0;
                    states = jsonObject.getInt("state");

                    switch (states) {
                        case 1:
                            toastShow("Save to your profile Successfully");
                            break;
                        case -1:
                            toastShow("Save Faild. This entity is already saved in your account");
                            break;
                        case 7:
                            toastShow("Save Failed, inputs can't be empty, try again");
                            break;
                        case 9:
                            toastShow("Save Failed, try again");
                            break;
                        default:
                            toastShow("Error");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void toastShow(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Species> removeDuplicateRecord(ArrayList<Species> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getCommon().equals(list.get(i).getCommon())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

//    public ArrayList<Species> removeDuplicateRecord(ArrayList<Species> arrayList){
//        for (int i = 0; i < arrayList.size() - 1; i++){
//            if (arrayList.get(i).getCommon().equals(arrayList.get(i + 1).getCommon())){
//                arrayList.remove(i);
//            }
//        }
//        return arrayList;
//    }
}
