package com.example.nydia.travelsmartapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.nydia.travelsmartapp.databinding.ActivityMainBinding;
import com.example.nydia.travelsmartapp.models.Carpark;
import com.example.nydia.travelsmartapp.models.CarparkAvailabilityResponse;
import com.example.nydia.travelsmartapp.models.CurrentLocationListener;
import com.example.nydia.travelsmartapp.models.DirectionsResults;
import com.example.nydia.travelsmartapp.models.Route;
import com.example.nydia.travelsmartapp.models.RouteDecode;
import com.example.nydia.travelsmartapp.models.TrafficCamera;
import com.example.nydia.travelsmartapp.models.TrafficIncident;
import com.example.nydia.travelsmartapp.models.TrafficIncidentResponse;
import com.example.nydia.travelsmartapp.viewmodel.TrafficCameraViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 16;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private final int overview = 0;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private Location mLastKnownLocation;

    private ImageView imgMyLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
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

    //private BottomSheetBehavior mBottomSheetBehavior;
    //private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private RecyclerView recyclerView;
    private Polyline polyline;
    private TrafficCameraViewModel viewModel;
    private List<TrafficCamera> trafficCamera = new ArrayList<>();
    private JourneyInfo journeyInfo;


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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.g_map);
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
                getDirectionsResults(origin,destination);
                //plotDirections(origin, destination, defaultTravelMode);


                //transportationtoogleFAB();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //BottomSheetDialogFragment bottomSheetDialogFragment = new TutsPlusBottomSheetDialogFragment();
        //bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        /*View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(300);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        addTabs(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        /*recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        TrafficCameraAdapter mAdapter = new TrafficCameraAdapter(trafficCamera);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
*/
        onSlideListener();

        viewModel =
                ViewModelProviders.of(this).get(TrafficCameraViewModel.class);

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
        journeyInfo = new JourneyInfo().newInstance(trafficCamera);
        adapter.addFrag(journeyInfo, "Journey Info");

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        //observeViewModel(viewModel);
        setupGoogleMapScreenSettings();
        intializeMapCamera();
        //getDeviceLocation();
    }


    private void plotDirections(DirectionsResults directions) {
        if (directions != null) {
            addPolyline(directions, mMap);

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
                //Log.d(TAG, "Current location:" + mLastKnownLocation);
                //mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        userLocationFAB();
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        //mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        //mUiSettings.setZoomControlsEnabled(true);
        //mUiSettings.setCompassEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        //mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void getDirectionsResults(String origin, String destination){
        viewModel.getDirectionsResultsObservable(origin, destination).observe(this, new Observer<DirectionsResults>() {
            @Override
            public void onChanged(@Nullable DirectionsResults directionsResults) {
                plotDirections(directionsResults);
            }
        });
    }

    /*private void addMarkersToMap(GoogleMapsAPI transitMode, GoogleMap mMap) {
        //Delete existing markers
        for(Marker marker : markers)
            marker.remove();

        ArrayList<MarkerOptions> decodedMarkers = transitMode.getMarkers();
        for (MarkerOptions marker : decodedMarkers)
            markers.add(mMap.addMarker(marker));

    }

    private void positionCamera(GoogleMapsAPI transitMode, GoogleMap mMap) {
        //TODO: ask whether camera should zoom to destination or to bounded box of directions
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(transitMode.getDirectionsBounds(), 100));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].endLocation.lat, route.legs[overview].endLocation.lng), 12));
    }

    private void addPolyline(GoogleMapsAPI transitMode, GoogleMap mMap) {
        //Delete existing polylines
        for(Polyline line: polylines)
            line.remove();
        polylines.clear();
        ArrayList<List<LatLng>> routeOptions = transitMode.getPolyLines();
        for (List<LatLng> decodedPath: routeOptions){
            //List<LatLng> decodedPath = PolyUtil.decode(results.routes[i].overviewPolyline.getEncodedPath());
            polylines.add(mMap.addPolyline(new PolylineOptions()
                    .addAll(decodedPath)
                    .width(20)
                    .color(Color.BLUE)
                    .geodesic(true)));
        }

    }*/

    private void addPolyline(DirectionsResults results, GoogleMap mMap) {
        //Delete existing polylines
        if (polyline != null)
            polyline.remove();
        Location location;
        Route route = results.getRoutes().get(0);
        String polylinestring = route.getOverviewPolyline().getPoints();
        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
        ArrayList<LatLng> decodelist = RouteDecode.decodePoly(polylinestring);
        routelist.add(new LatLng(route.getLegs().get(0).getStartLocation().getLat(), route.getLegs().get(0).getStartLocation().getLng()));
        routelist.addAll(decodelist);
        routelist.add(new LatLng(route.getLegs().get(0).getEndLocation().getLat(), route.getLegs().get(0).getEndLocation().getLng()));

        PolylineOptions decodedPath = new PolylineOptions().width(15).color(
                Color.RED);
        for (int i = 0; i < routelist.size(); i++) {
            decodedPath.add(routelist.get(i));
        }
        polyline = mMap.addPolyline(decodedPath);
        /*LatLng point = new LatLng(1.301921, 103.912905);
        boolean result = PolyUtil.isLocationOnPath(point, routelist, false, 100);
        if(result)
            mMap.addMarker(new MarkerOptions().position(point));
        point = new LatLng(1.297159, 103.851370);
        result = PolyUtil.isLocationOnPath(point, routelist, false, 100);
        if(result)
            mMap.addMarker(new MarkerOptions().position(point));*/
        //getNearestCameras(viewModel, routelist);
        getNearestIncidents(viewModel, routelist);
        getNearestCameras(viewModel, routelist);
        //getCarparkAvailability(viewModel);
        getNearestAvailableCarpark(viewModel, new LatLng(route.getLegs().get(0).getEndLocation().getLat(), route.getLegs().get(0).getEndLocation().getLng()));
    }

    private void getNearestIncidents(TrafficCameraViewModel viewModel, ArrayList<LatLng> routelist) {
        viewModel.getNearestIncidentsObservable(routelist).observe(this, new Observer<List<TrafficIncident>>() {

            @Override
            public void onChanged(@Nullable List<TrafficIncident> incidents) {
                if (incidents.size() > 0) {
                    Log.d(TAG, "Nearest incident");
                    for (TrafficIncident incident: incidents) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(incident.getLatitude(), incident.getLongitude())).title(incident.getMessage()));
                    }
                    //showTrafficIncidents(incidents);
                }
            }
        });
    }

    private void getNearestCameras(TrafficCameraViewModel viewModel, ArrayList<LatLng> routelist) {
        viewModel.getNearestCamerasObservable(routelist).observe(this, new Observer<List<TrafficCamera>>() {

            @Override
            public void onChanged(@Nullable List<TrafficCamera> cameras) {
                if (cameras.size() > 0) {
                    Log.d(TAG, "Nearest cameras");
                    //recyclerView.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "Updating recyclerview");
                trafficCamera.clear();
                trafficCamera.addAll(cameras);
                journeyInfo.setTrafficCameras((ArrayList)trafficCamera);
                //recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void getNearestAvailableCarpark(final TrafficCameraViewModel viewModel, LatLng destination) {
        viewModel.getNearestAvailableCarparkObservable(destination).observe(this, new Observer<List<Carpark>>() {

            @Override
            public void onChanged(@Nullable List<Carpark> carparks) {
                if (carparks.size() > 0) {
                    for (Carpark carpark : carparks) {
                        getAddress(viewModel, carpark.getGeometries().get(0).getCoordinates());
                        //String address = getAddress(viewModel, carpark.getGeometries().get(0).getCoordinates());
                        //mMap.addMarker(new MarkerOptions().position(latlng).title(address));
                    }
                }
                //showTrafficIncidents(incidents);
            }
        });
    }

    private void getAddress(TrafficCameraViewModel viewModel, final String coordinates) {
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

    private String getEndLocationTitle(DirectionsResult results) {
        //TODO: these results will be displayed in SlideUpPanel
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable + " Arrival time :" + results.routes[overview].legs[overview].arrivalTime; //TODO: don't use arrivalTime
    }

    private void getIncidents(TrafficCameraViewModel viewModel) {
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

    private void getCarparkAvailability(TrafficCameraViewModel viewModel) {
        viewModel.getCarparkAvailabilityObservable().observe(this, new Observer<CarparkAvailabilityResponse>() {

            @Override
            public void onChanged(@Nullable CarparkAvailabilityResponse carparkAvailability) {
                if (carparkAvailability != null) {
                    showCarparkAvailability(carparkAvailability);
                    }
                    //showTrafficIncidents(incidents);
            }
        });
    }

    private void showCarparkAvailability(CarparkAvailabilityResponse carparkAvailability) {
        for (int i = 0; i < carparkAvailability.getResult().size(); i++) {
            if (carparkAvailability.getResult().get(i).getGeometries() != null) {
                Carpark.Geometry carpark = carparkAvailability.getResult().get(i).getGeometries().get(0);
                Double lat = carpark.getLatitude();
                Double lng = carpark.getLongitude();
                //Log.d(TAG, "Carpark available: " + lat + "," + lng);
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            }
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
}


