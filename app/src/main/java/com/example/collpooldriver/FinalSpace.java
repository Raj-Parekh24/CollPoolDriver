package com.example.collpooldriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FinalSpace extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawer;
    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation=location;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),Default_Zoom));
        GeoFire geoFire=new GeoFire(databaseReference);
        String userid=firebaseAuth.getCurrentUser().getUid();
        geoFire.setLocation(userid, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
               // Toast.makeText(FinalSpace.this,"GeoFire Complete",Toast.LENGTH_SHORT).show();
            }
        });
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
    private FusedLocationProviderClient mFusedLocationProviderClient; // to get current location
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList; // to get predictions
    private Location mLastKnownLocation; // for getting current location
    private LocationCallback locationCallback; //for changing location
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private final float Default_Zoom=30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_space);

        //gettting firebase instances and references

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance("https://coll-pool-driver.firebaseio.com/");//+firebaseAuth.getCurrentUser().getUid()+"/User");
        databaseReference=firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        mapView=mapFragment.getView();

        mFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(FinalSpace.this);
        Places.initialize(FinalSpace.this,"AIzaSyATW0zskeHxoGG_PV0mViZNvhSkW5cdGXY");
        placesClient=Places.createClient(this);
        final AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();

        //for making navigation drawer object

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //////////////////////////////////////////////////////////////////////////
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {// for working on menu buttons
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        AlertDialog.Builder builder=new AlertDialog.Builder(FinalSpace.this);
                        builder.setTitle("Log-Out from Coll Driver").setMessage("Are you sure you wan to Log-Out ").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                            }
                        }).setNegativeButton("No", null);
                        AlertDialog show1=builder.create();
                        show1.show();
                        break;
                    }
                    case R.id.helpmail: {
                        Toast.makeText(FinalSpace.this, "Mail us selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.helpcall: {
                        Toast.makeText(FinalSpace.this, "Call us selected", Toast.LENGTH_SHORT).show();
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

   @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap = googleMap;
       buildGoogleApiClient();
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

            }
        }
    }

    // inner method to get current location of user
    /*private void getLocation(){
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {//helps to get last known location is user is offline
            @Override
            public void onComplete(@NonNull Task<Location> task) {
             if(task.isSuccessful()){
                 mLastKnownLocation=task.getResult();
                 if(mLastKnownLocation!=null){
                     LocationRequest locationRequest=LocationRequest.create();//if online ask for location continously
                     locationRequest.setInterval(1000);
                     locationRequest.setFastestInterval(500);
                     locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                     locationCallback=new LocationCallback(){
                         @Override
                         public void onLocationResult(LocationResult locationResult) {
                             super.onLocationResult(locationResult);
                             if(locationResult==null){
                                 return;
                             }
                             mLastKnownLocation=locationResult.getLastLocation();
                             //updating user location

                             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),Default_Zoom));
                             mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                         }
                     };
                     mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),Default_Zoom));

                     // DatabaseReference toRoot=databaseReference.child("Users").push();


                 }else {
                     LocationRequest locationRequest=LocationRequest.create();//if online ask for location continously
                     locationRequest.setInterval(1000);
                     locationRequest.setFastestInterval(500);
                     locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                     locationCallback=new LocationCallback(){
                         @Override
                         public void onLocationResult(LocationResult locationResult) {
                             super.onLocationResult(locationResult);
                             if(locationResult==null){
                                 return;
                             }
                             mLastKnownLocation=locationResult.getLastLocation();
                             //updating user location
                             // DatabaseReference toRoot=databaseReference.child("Users").push();
                             GeoFire geoFire=new GeoFire(databaseReference);
                             String userid=firebaseAuth.getCurrentUser().getUid();
                             geoFire.setLocation(userid, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener() {
                                 @Override
                                 public void onComplete(String key, DatabaseError error) {
                                     Toast.makeText(FinalSpace.this,"GeoFire Complete",Toast.LENGTH_SHORT).show();
                                 }
                             });

                             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),Default_Zoom));
                             mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                         }
                     };
                     mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
                 }
             }
             else {
                 Toast.makeText(FinalSpace.this,"Location Unknown",Toast.LENGTH_SHORT).show();
             }
            }
        });
    }*/

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
        //for removing user from server side
        //DatabaseReference toRoot=databaseReference.child("Users").push();
        GeoFire geoFire=new GeoFire(databaseReference);
        String userid=firebaseAuth.getCurrentUser().getUid();
        geoFire.removeLocation(userid);
    }
}
