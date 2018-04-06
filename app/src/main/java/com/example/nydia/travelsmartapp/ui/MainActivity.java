package com.example.nydia.travelsmartapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.nydia.travelsmartapp.R;
import com.example.nydia.travelsmartapp.databinding.ActivityMainBinding;
import com.example.nydia.travelsmartapp.models.Bounds;
import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.example.nydia.travelsmartapp.models.CurrentLocationListener;
import com.example.nydia.travelsmartapp.models.DirectionsResults;
import com.example.nydia.travelsmartapp.models.Forecast;
import com.example.nydia.travelsmartapp.models.Leg;
import com.example.nydia.travelsmartapp.models.PlanningArea;
import com.example.nydia.travelsmartapp.models.Route;
import com.example.nydia.travelsmartapp.models.RouteDecode;
import com.example.nydia.travelsmartapp.models.TrafficCamera;
import com.example.nydia.travelsmartapp.models.TrafficIncident;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;
import com.example.nydia.travelsmartapp.viewmodel.MainViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.DirectionsResult;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 16;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private final int overview = 0;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private Location mLastKnownLocation;

    private LatLng mDefaultLocation = new LatLng(1.348482, 103.683049);

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private String origin;
    private String destination;

    private List<Polyline> polylines = new ArrayList<>();

    private List<Marker> markers = new ArrayList<>();

    private boolean defaultTravelMode = true; //default is driving

    private boolean mGpsOn = false;
    private SlidingUpPanelLayout slidingLayout;

    private SupportMapFragment mapFragment;

    private PlaceAutocompleteFragment autocompleteFragment;
    private RelativeLayout relativeLayout;

    //private BottomSheetBehavior mBottomSheetBehavior;
    //private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private Polyline polyline;
    private MainViewModel viewModel;
    private List<TrafficCamera> trafficCamera = new ArrayList<>();
    private JourneyInfoFragment journeyInfoFragment;
    private List<TrafficIncident> trafficIncident = new ArrayList<>();
    private DestinationInfoFragment destinationInfoFragment;
    private List<Carpark> availableCarparks = new ArrayList<>();
    private Leg.StartLocation startLocation;
    private Leg.EndLocation endLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //setContentView(R.layout.activity_main);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.setAnchorPoint(0.4f);
        slidingLayout.addPanelSlideListener(onSlideListener());

        mGpsOn = checkLocationSettings();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Enter Destination");
        autocompleteFragment.setFilter(new AutocompleteFilter.Builder()
                .setCountry("SG") //limit queries only to Singapore locations
                .build());

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //toggleTransportation();
                LatLng currentLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                origin = currentLocation.latitude + "," + currentLocation.longitude;
                destination = place.getLatLng().latitude + "," + place.getLatLng().longitude;

                Log.i(TAG, "Place: " + place.getAddress().toString());
                Log.i(TAG, "Place coordinates: " + place.getLatLng().toString());
                getDirectionsResults(origin,destination);
                //transportationtoogleFAB();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        relativeLayout = (RelativeLayout) findViewById(R.id.sliding_panel);
        relativeLayout.setVisibility(View.VISIBLE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        addTabs(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        onSlideListener();

        viewModel =
                ViewModelProviders.of(this).get(MainViewModel.class);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState);
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        journeyInfoFragment = new JourneyInfoFragment().newInstance(new Leg(), trafficCamera, trafficIncident);
        adapter.addFrag(journeyInfoFragment, "Journey Info");
        destinationInfoFragment = new DestinationInfoFragment().newInstance(new Forecast(), availableCarparks);
        adapter.addFrag(destinationInfoFragment, "destination Info");
        viewPager.setAdapter(adapter);
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                //textView.setText("panel is sliding");
                autocompleteFragment.setMenuVisibility(false);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //Log.d(TAG, "onPanelStateChanged: " + newState);
                //Log.d(TAG, "autocompleteFragment: " + autocompleteFragment.isVisible());

                //TODO: better animation?
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    autocompleteFragment.getView().setVisibility(View.GONE);
                else
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
            }
        };
    }

    private boolean checkLocationSettings() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
            return false;
        }
        return true;
    }

    private void getDeviceLocation() {
        if (mLocationPermissionGranted) {
            CurrentLocationListener.getInstance(getApplicationContext()).observe(this, new Observer<Location>() {

                @Override
                public void onChanged(@Nullable Location location) {
                    if (location != null) {
                        Log.d(TAG, "onChanged:location updated " + location);
                        //getWeather(location.getLatitude(), location.getLongitude());
                        mLastKnownLocation = location;
                        //CurrentLocationListener.getInstance(getApplicationContext()).removeObserver(this);
                    } else
                        Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show();
                }
            });
        } else getLocationPermission();
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            mLocationPermissionGranted = false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        //mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                    mMap.setMyLocationEnabled(true);
                    userLocationFAB();
                    intializeMapCamera();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        setupGoogleMapScreenSettings();
        intializeMapCamera();
        //getDeviceLocation();
    }

    private void intializeMapCamera(){
        if (mMap == null) {
            return;
        }
        //initiate map camera
        if (mLastKnownLocation != null) {
            Log.d(TAG, "Camera zooming to Last Known Location");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else if (mCameraPosition != null) {
            Log.d(TAG, "Camera zooming to saved camera position");
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else {
            Log.d(TAG, "Camera zooming to saved default location");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

        }
    }

    private void setupGoogleMapScreenSettings() {
        if (mMap == null) {
            return;
        }
        getLocationPermission();
        try {
            //TODO: camera zoom in default should be user's location
            if (mLocationPermissionGranted) {
                getDeviceLocation();
                mMap.setMyLocationEnabled(true);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        userLocationFAB();
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        //mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void plotDirections(DirectionsResults directions) {
        if (directions != null) {
            ArrayList<LatLng> routelist = initDirections(directions);
            setupTabs(routelist);
            addPolyline(routelist);
            addMarkersToMap(startLocation, endLocation);
            positionCamera(directions);
            slidingLayout.setPanelHeight(200);
            //TextView testview = (TextView)findViewById(R.id.testview);
            //testview.setText(directions.routes[overview].legs[overview].steps[overview].htmlInstructions);
            /*movieList.clear();
            for (int i=0; i<directions.routes[overview].legs[overview].steps.length; i++) {
                if (publicTransport) {
                    //TODO: implement details on directions for public transportation
                    for (int j = 0; j < directions.routes[overview].legs[overview].steps[i].steps.length; j++)
                        movieList.add(new Movie(directions.routes[overview].legs[overview].steps[i].steps[j].transitDetails., "", ""));
                }
                //TODO: deal with the HTML output
                movieList.add(new Movie(directions.routes[overview].legs[overview].steps[i].htmlInstructions, "", ""));
            }
                mAdapter.notifyDataSetChanged();*/
        }
    }

    private ArrayList<LatLng> initDirections(DirectionsResults directions) {
        Route route = directions.getRoutes().get(0);
        String polylinestring = route.getOverviewPolyline().getPoints();
        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
        startLocation = route.getLegs().get(0).getStartLocation();
        endLocation = route.getLegs().get(0).getEndLocation();
        ArrayList<LatLng> decodelist = RouteDecode.decodePoly(polylinestring);
        routelist.add(new LatLng(startLocation.getLat(), startLocation.getLng()));
        routelist.addAll(decodelist);
        routelist.add(new LatLng(endLocation.getLat(), endLocation.getLng()));
        return routelist;
    }

    private void setupTabs(ArrayList<LatLng> routelist){
        getPlanningArea(viewModel, endLocation.getLat(), endLocation.getLng());
        getNearestIncidents(viewModel, routelist);
        getNearestCameras(viewModel, routelist);
        getNearestAvailableCarpark(viewModel, new LatLng(endLocation.getLat(), endLocation.getLng()));
    }

    private void getDirectionsResults(String origin, String destination){
        viewModel.getDirectionsResultsObservable(origin, destination).observe(this, new Observer<DirectionsResults>() {
            @Override
            public void onChanged(@Nullable DirectionsResults directionsResults) {
                plotDirections(directionsResults);
                journeyInfoFragment.setLeg(directionsResults.getRoutes().get(0).getLegs().get(0));
            }
        });
    }

    private void addMarkersToMap(Leg.StartLocation startLocation, Leg.EndLocation endLocation) {
        //Delete existing markers
        for(Marker marker : markers)
            marker.remove();

        //ArrayList<MarkerOptions> decodedMarkers = transitMode.getMarkers();
        MarkerOptions startMarker = new MarkerOptions().position(new LatLng(startLocation.getLat(), startLocation.getLng()));
        MarkerOptions endMarker = new MarkerOptions().position(new LatLng(endLocation.getLat(), endLocation.getLng()));
        markers.add(mMap.addMarker(startMarker));
        markers.add(mMap.addMarker(endMarker));
    }

    private void positionCamera(DirectionsResults directions) {
        //TODO: ask whether camera should zoom to destination or to bounded box of directions
        Bounds boundary = directions.getRoutes().get(0).getBounds();
        LatLng northeast = new LatLng(boundary.getNortheast().getLat(), boundary.getNortheast().getLng());
        LatLng southwest = new LatLng(boundary.getSouthwest().getLat(), boundary.getSouthwest().getLng());
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast), 100));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].endLocation.lat, route.legs[overview].endLocation.lng), 12));
    }

    private void addPolyline(ArrayList<LatLng> routelist) {
        //Delete existing polylines
        if (polyline != null)
            polyline.remove();

        PolylineOptions decodedPath = new PolylineOptions()
                .width(20)
                .color(Color.BLUE)
                .geodesic(true);
        for (int i = 0; i < routelist.size(); i++) {
            decodedPath.add(routelist.get(i));
        }
        polyline = mMap.addPolyline(decodedPath);
    }

    private void getNearestIncidents(MainViewModel viewModel, ArrayList<LatLng> routelist) {
        viewModel.getNearestIncidentsObservable(routelist).observe(this, new Observer<List<TrafficIncident>>() {

            @Override
            public void onChanged(@Nullable List<TrafficIncident> incidents) {
                if (incidents.size() > 0) {
                    Log.d(TAG, "Nearest incident");
                    for (TrafficIncident incident: incidents) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(incident.getLatitude(), incident.getLongitude())).title(incident.getMessage()));
                    }
                }
                Log.d(TAG, "Updating traffic incident display");
                trafficIncident.clear();
                trafficIncident.addAll(incidents);
                journeyInfoFragment.setTrafficIncidents((ArrayList)trafficIncident);
            }
        });
    }

    private void getNearestCameras(MainViewModel viewModel, ArrayList<LatLng> routelist) {
        viewModel.getNearestCamerasObservable(routelist).observe(this, new Observer<List<TrafficCamera>>() {

            @Override
            public void onChanged(@Nullable List<TrafficCamera> cameras) {
                if (cameras.size() > 0) {
                }
                Log.d(TAG, "Updating traffic camera display");
                trafficCamera.clear();
                trafficCamera.addAll(cameras);
                journeyInfoFragment.setTrafficCameras((ArrayList)trafficCamera);
            }
        });
    }

    private void getNearestAvailableCarpark(final MainViewModel viewModel, LatLng destination) {
        viewModel.getNearestAvailableCarparkObservable(destination).observe(this, new Observer<List<Carpark>>() {

            @Override
            public void onChanged(@Nullable List<Carpark> carparks) {
                if (carparks.size() > 0) {
                    for (Carpark carpark : carparks) {
                        Double lat = carpark.getLatitude();
                        Double lng = carpark.getLongitude();
                        //Log.d(TAG, "Carpark available: " + lat + "," + lng);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(carpark.getDevelopment()));

                    }
                }
                Log.d(TAG, "Updating carparks display");
                availableCarparks.clear();
                availableCarparks.addAll(carparks);
                destinationInfoFragment.setCarparks((ArrayList)availableCarparks);
            }
        });
    }

    private void getAddress(MainViewModel viewModel, final String coordinates) {
        viewModel.getAddress(coordinates).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String reverseGeocoding) {
                if (reverseGeocoding != null) {
                    String[] latlong = coordinates.split(",");
                    LatLng latlng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
                    mMap.addMarker(new MarkerOptions().position(latlng).title(reverseGeocoding));

                }}
                //showTrafficIncidents(incidents);
        });
    }

    private void getPlanningArea(final MainViewModel viewModel, Double lat, Double lng){
        viewModel.getPlanningArea(lat,lng).observe(this, new Observer<List<PlanningArea>>() {
            @Override
            public void onChanged(@Nullable List<PlanningArea> planningArea) {
                getWeatherForecast(viewModel, planningArea.get(0).getPlnAreaN());
                getPsi(viewModel, planningArea.get(0).getPlnAreaN());
            }
            //showTrafficIncidents(incidents);
        });
    }

    private void getWeatherForecast(MainViewModel viewModel, String area){
        viewModel.getWeatherForecast(area).observe(this, new Observer<Forecast>() {
            @Override
            public void onChanged(@Nullable Forecast forecast) {
                if(forecast!=null)
                    destinationInfoFragment.setForecast(forecast);
            }
        });
    }

    private void getPsi(MainViewModel viewModel, String area){
        viewModel.getPsi(area).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String psi) {
                if(psi!=null)
                    destinationInfoFragment.setPsi(psi);
            }
        });
    }


    private String getEndLocationTitle(DirectionsResult results) {
        //TODO: these results will be displayed in SlideUpPanel
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable + " Arrival time :" + results.routes[overview].legs[overview].arrivalTime; //TODO: don't use arrivalTime
    }

    private void getIncidents(MainViewModel viewModel) {
        viewModel.getTrafficIncidentObservable().observe(this, new Observer<TrafficIncidentResponse>() {

            @Override
            public void onChanged(@Nullable TrafficIncidentResponse incidents) {
                if (incidents != null) {
                    Log.d(TAG, "New incident");
                    for (int i = 0; i < incidents.getTrafficIncidents().size(); i++) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(incidents.getTrafficIncidents().get(i).getLatitude(), incidents.getTrafficIncidents().get(i).getLongitude())).title(incidents.getTrafficIncidents().get(i).getMessage()));
                    }
                    showTrafficIncidents(incidents);
                }
            }
        });
    }

    private void getCarparkAvailability(MainViewModel viewModel) {
        viewModel.getCarparkAvailabilityObservable().observe(this, new Observer<CarparkAvailabilityResponse>() {

            @Override
            public void onChanged(@Nullable CarparkAvailabilityResponse carparkAvailability) {
                if (carparkAvailability != null) {
                    Log.i(TAG, "Carpark available " + carparkAvailability.getValue().get(0).getLocation());
                    showCarparkAvailability(carparkAvailability);
                    }
                    //showTrafficIncidents(incidents);
            }
        });
    }

    private void showCarparkAvailability(CarparkAvailabilityResponse carparkAvailability) {
        for (int i = 0; i < carparkAvailability.getValue().size(); i++) {
            Carpark carpark = carparkAvailability.getValue().get(i);
            Double lat = carpark.getLatitude();
            Double lng = carpark.getLongitude();
            //Log.d(TAG, "Carpark available: " + lat + "," + lng);
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(carpark.getDevelopment()));

        }
    }

    private void showTrafficIncidents(TrafficIncidentResponse incidents) {
        if (incidents != null) {
            //Log.d(TAG, "Incident: " + incidents.get(0).getMessage());
            for (int i = 0; i < incidents.getTrafficIncidents().size(); i++) {
                TrafficIncident incident = incidents.getTrafficIncidents().get(i);
                Double lat = incident.getLatitude();
                Double lng = incident.getLongitude();
                //incidentslocation.add(new LatLng(lat, lng));
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).snippet(incident.getMessage()));
            }
        }
    }


    private void userLocationFAB() {
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationPermissionGranted == true && mGpsOn == true) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));
                } else {
                    mGpsOn = checkLocationSettings();
                    getLocationPermission();
                }
            }
        });
    }

    /*private void toggleTransportation() {
        ToggleButton simpleToggleButton = (android.widget.ToggleButton) findViewById(R.id.button_favorite); // initiate a toggle button
        //simpleToggleButton.setVisibility(View.GONE); //hide toggle button
        simpleToggleButton.setEnabled(true);
        simpleToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    defaultTravelMode = TravelMode.TRANSIT;
                } else {
                    defaultTravelMode = TravelMode.DRIVING;

                }
                if (destination!=null && origin!=null){
                    DirectionsResult results = getDirectionsDetails(origin, destination, defaultTravelMode);
                    plotDirections(results, defaultTravelMode == TravelMode.TRANSIT);
                }
            }
        });

    }*/



    /*private void transportationtoogleFAB(){
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.toggleButton);
        FAB.setEnabled(true);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultTravelMode == TravelMode.TRANSIT)
                    defaultTravelMode = TravelMode.DRIVING;
                else defaultTravelMode = TravelMode.TRANSIT;
                if (origin != null && destination != null) {
                    DirectionsResult results = getDirectionsDetails(origin, destination, defaultTravelMode);
                    plotDirections(results);

                } else {
                    return;
                }
            }
        });
    }*/


}


