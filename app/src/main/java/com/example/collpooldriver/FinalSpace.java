package com.example.collpooldriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalSpace extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private boolean ongostart=false;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private String custmoerid;
    private AlertDialog show1;
    private boolean checker;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,driveravailability,userDataBaseReference;
    private FirebaseAuth firebaseAuth,seconadryAuth;
    private DrawerLayout drawer;
    @Override
    public void onLocationChanged(Location location) {
        if(mLastKnownLocation==null){
            mLastKnownLocation=location;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(Default_Zoom));
        }
        mLastKnownLocation=location;
        //
        if(!checker){
            GeoFire geoFire=new GeoFire(databaseReference.child("Driver's Location"));
            String userid=firebaseAuth.getCurrentUser().getUid();
            geoFire.setLocation(userid, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    // Toast.makeText(FinalSpace.this,String.valueOf(checker),Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(ongostart){
            GeoFire geoFire=new GeoFire(driveravailability);
            String userid=firebaseAuth.getCurrentUser().getUid();
            geoFire.setLocation(userid, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    // Toast.makeText(FinalSpace.this,String.valueOf(checker),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,mLocationRequest,FinalSpace.this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList; // to get predictions
    private Location mLastKnownLocation; // for getting current location
    private LocationCallback locationCallback; //for changing location
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private FirebaseApp secondary;
    private final float Default_Zoom=30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_space);

        //gettting firebase instances and references

        checker=false;
        FirebaseOptions options = new FirebaseOptions.Builder().setApplicationId("1:609445212751:android:b4bc76812d268be4").setApiKey("AIzaSyAed5oNRaezl9-b4n2nIbijx3YUGY2NzVA").setDatabaseUrl("https://collpool2019-2fe22.firebaseio.com").build();
        FirebaseApp.initializeApp(this,options,"secondary");
        secondary=FirebaseApp.getInstance("secondary");
        seconadryAuth=FirebaseAuth.getInstance(secondary);
        userDataBaseReference=FirebaseDatabase.getInstance(secondary).getReference();

        // chnaginng drawer attributes
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance("https://coll-pool-driver.firebaseio.com/");//+firebaseAuth.getCurrentUser().getUid()+"/User");
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        driveravailability=firebaseDatabase.getReference("Driver Availability");
        displayName();//functions displays naeme on side bar
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        mapView=mapFragment.getView();

        final AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();

        //for making navigation drawer object
        mLastKnownLocation=null;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //////////////////////////////////////////////////////////////////////////
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {// for working on menu buttons
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(FinalSpace.this,"Hello",Toast.LENGTH_LONG).show();
                switch (item.getItemId()) {
                    case R.id.userprofile: {
                        Toast.makeText(FinalSpace.this, "User Settings selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.your_trips: {
                        Toast.makeText(FinalSpace.this, "Trips show selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.wallet: {
                        Toast.makeText(FinalSpace.this, "Wallet selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.logout:{
                        final ProgressDialog progressDialog=new ProgressDialog(FinalSpace.this);
                        progressDialog.setMessage("Logging Out");
                        AlertDialog.Builder builder=new AlertDialog.Builder(FinalSpace.this);
                        builder.setTitle("Log-Out from Coll Driver").setMessage("Are you sure you wan to Log-Out ").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();
                                checker=true;
                                finish();
                                progressDialog.dismiss();
                            }
                        }).setNegativeButton("No", null);
                        show1=builder.create();
                        show1.show();
                        break;
                    }
                    case R.id.helpmail: {
                        sendMail();
                        break;
                    }
                    case R.id.helpcall: {
                        if(ContextCompat.checkSelfPermission(FinalSpace.this, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                            String dial="tel:"+"09825059546";
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }else {
                            Toast.makeText(FinalSpace.this, "Permission is denied from your side.Please go to settings to allows us to make phone call", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }

                }
                return true;
            }
        });

        //for getting auto suggestions
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(),true,null,true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if(buttonCode==MaterialSearchBar.BUTTON_NAVIGATION){
                    drawer = findViewById(R.id.draw_layout);
                    drawer.openDrawer(GravityCompat.START);

                }
                else if (buttonCode==MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.disableSearch();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FindAutocompletePredictionsRequest requests=FindAutocompletePredictionsRequest.builder()
                        .setCountry("IND")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token).setQuery(charSequence.toString()).build();

                placesClient.findAutocompletePredictions(requests).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse responses=task.getResult();
                            if(responses!=null){
                                predictionList=responses.getAutocompletePredictions();
                                List<String> transfer=new ArrayList<>();
                                for(AutocompletePrediction s:predictionList){
                                    transfer.add(s.getFullText(null).toString());
                                }

                                materialSearchBar.updateLastSuggestions(transfer);
                                if(!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        }
                        else {
                            Toast.makeText(FinalSpace.this,"Prediction failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //for transferring to suggested place clicked by user
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if(position>=predictionList.size()){
                    Toast.makeText(FinalSpace.this,"Unable to find",Toast.LENGTH_SHORT).show();
                    return;
                }

                String suggestion=materialSearchBar.getLastSuggestions().get(position).toString();
                AutocompletePrediction selectedPrediction=predictionList.get(position);

                materialSearchBar.setText(suggestion);
                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  materialSearchBar.clearSuggestions();
                                              }
                                          },1000
                );



                //After selecting suggestion close keyboard code
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                //passing querry to google

                final FetchPlaceRequest fetchPlaceRequest=FetchPlaceRequest.builder(selectedPrediction.getPlaceId(),Arrays.asList(Place.Field.LAT_LNG)).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place=fetchPlaceResponse.getPlace();
                        LatLng destination=place.getLatLng();
                        if(destination!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination,Default_Zoom));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException){

                            Toast.makeText(FinalSpace.this,"Place not found",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });


    }

    private void sendMail() {
        String mail="mailto:"+"collpool2019@gmail.com";
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(mail));
        startActivity(Intent.createChooser(intent,"Choose email client"));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ActivityCompat.checkSelfPermission(FinalSpace.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (FinalSpace.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            return;
        }
        buildGoogleApiClient();
        //changing ui settings
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(mapView!=null && mapView.findViewById(Integer.parseInt("1"))!=null){
            View locationButton=((View)mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,180);
        }

        //check if gps is enabled or not

        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);


        SettingsClient settingsClient= LocationServices.getSettingsClient(FinalSpace.this);
        Task<LocationSettingsResponse> task=settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(FinalSpace.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //getLocation();
            }
        });

        task.addOnFailureListener(FinalSpace.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException=(ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(FinalSpace.this,143);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {//to remove search history in between
            @Override
            public boolean onMyLocationButtonClick() {
                if(materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if(materialSearchBar.isSearchEnabled())
                    materialSearchBar.disableSearch();
                return false;
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient=new GoogleApiClient.Builder(FinalSpace.this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==143){
            if(resultCode==RESULT_OK){
                buildGoogleApiClient();
            }
        }
    }

    //for closing the drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GeoFire geoFire=new GeoFire(driveravailability);
        geoFire.removeLocation(firebaseAuth.getCurrentUser().getUid(), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
        if(checker){
            FirebaseAuth.getInstance().signOut();
            show1.dismiss();
            startActivity(new Intent(FinalSpace.this,MainActivity.class));
        }
    }

    public void onGo(View view){
        ongostart=true;
        String driverid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference torides = FirebaseDatabase.getInstance().getReference().child("On Going Driver").child(driverid);
        torides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> x = (Map<String, Object>) dataSnapshot.getValue();
                    if (x.get("CustomerRideID") != null) {
                        custmoerid = x.get("CustomerRideID").toString();
                        ongostart=false;
                        GeoFire geoFire=new GeoFire(driveravailability);
                        geoFire.removeLocation(firebaseAuth.getCurrentUser().getUid(), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });

                        getAssignedPickUpLocation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    Context context = this;
    private void getAssignedPickUpLocation(){
        Toast.makeText(FinalSpace.this,custmoerid.toString(),Toast.LENGTH_LONG).show();
        DatabaseReference torides=userDataBaseReference.child(custmoerid).child(custmoerid).child("l");
        torides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> a=(List<Object>)dataSnapshot.getValue();
                double lat=0;double lang=0;
                if(a.get(0)!=null){
                    lat=Double.parseDouble(a.get(0).toString());
                }
                if(a.get(1)!=null){
                    lang=Double.parseDouble(a.get(1).toString());
                }

                LatLng DriverLocation=new LatLng(lat,lang);

                Location location1=new Location("");
                location1.setLatitude(mLastKnownLocation.getLatitude());
                location1.setLongitude(mLastKnownLocation.getLongitude());

                Location location2=new Location("");
                location2.setLatitude(DriverLocation.latitude);
                location2.setLongitude(DriverLocation.longitude);

                float Distance=location1.distanceTo(location2);
                if(Distance<=200) {
                    Toast.makeText(FinalSpace.this, "Nirma Go", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Nirma Go");
                    GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
                    String serverKey = "AIzaSyDDKY2cFvErpEdM3FgzH117moVm_1us0Fw";

                    LatLng origin = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                    LatLng destination = new LatLng(23.1284,72.5449);

                    GoogleDirection.withServerKey(serverKey).from(origin).to(destination).alternativeRoute(true).transportMode(TransportMode.DRIVING).language(Language.ENGLISH).execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction) {

                            String status = direction.getStatus();

                            if(status.equals(RequestResult.OK)) {

                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                List<Step> step=leg.getStepList();
                                ArrayList<LatLng> pointList = leg.getDirectionPoint();
                                Info distanceInfo = leg.getDistance();
                                Info durationInfo = leg.getDuration();
                                String distance = distanceInfo.getText();
                                String duration = durationInfo.getText();

                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, Color.GREEN);
                                mMap.addPolyline(polylineOptions);

                                List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(context, stepList, 5, Color.GREEN, 3, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    mMap.addPolyline(polylineOption);
                                }
                            }
                            else if(status.equals(RequestResult.NOT_FOUND)) {
                            }
                        }
                        @Override
                        public void onDirectionFailure(Throwable t) {

                        }
                    });
                }

                else{
                    GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
                    String serverKey = "AIzaSyDDKY2cFvErpEdM3FgzH117moVm_1us0Fw";

                    LatLng origin = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                    LatLng destination = new LatLng(lat,lang);

                    GoogleDirection.withServerKey(serverKey).from(origin).to(destination).alternativeRoute(true).transportMode(TransportMode.DRIVING).language(Language.ENGLISH).execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction) {

                            String status = direction.getStatus();

                            if(status.equals(RequestResult.OK)) {

                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                List<Step> step=leg.getStepList();
                                ArrayList<LatLng> pointList = leg.getDirectionPoint();
                                Info distanceInfo = leg.getDistance();
                                Info durationInfo = leg.getDuration();
                                String distance = distanceInfo.getText();
                                String duration = durationInfo.getText();

                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, Color.GREEN);
                                mMap.addPolyline(polylineOptions);

                                List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(context, stepList, 5, Color.GREEN, 3, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    mMap.addPolyline(polylineOption);
                                }
                            }
                            else if(status.equals(RequestResult.NOT_FOUND)) {
                            }
                        }
                        @Override
                        public void onDirectionFailure(Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static final String TAG = "MyActivity";

    private void displayName(){
        databaseReference.child("User").child("Details").child("Username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String x=dataSnapshot.getValue(String.class);
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.UserNamed);
                navUsername.setText(x);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}