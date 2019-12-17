package com.example.collpooldriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.InetAddresses;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class FinalSpace extends AppCompatActivity implements OnMapReadyCallback {
    private DrawerLayout drawer;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private double clatitude,clongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_final_space);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        /////////////////////////////////////////////////////
        checkNetwork();
    }


    //check wether gps is enabled or not;
    private boolean checkGps()
    { LocationManager locationManager1 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.gps_not_found_title)  // GPS not found
                    .setMessage(R.string.gps_not_found_message) // Want to enable?
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intenta=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intenta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intenta);
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return false;
        }
        return true;
    }


    private boolean checkNetwork()
    {
        boolean a=false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())
        {
            a=true;
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.location_not_found_title)  // GPS not found
                    .setMessage(R.string.location_not_found_message) // Want to enable?
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intenta=new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                            intenta.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intenta);
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
        return a;
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(23.1284, 72.5449);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setMaxZoomPreference(14.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.2f));
    }

    public void onOpenMenu(View view)
    {
        drawer = findViewById(R.id.draw_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    public void onGpsButton(android.view.View view)
    {
        if(checkGps()) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    clatitude = location.getLatitude();
                    clongitude = location.getLongitude();
                    // Toast.makeText(FinalSpace.this,clatitude+" "+clongitude,Toast.LENGTH_SHORT).show();
                    LatLng userlatLang=new LatLng(clatitude,clongitude);
                    mMap.addMarker(new MarkerOptions().position(userlatLang).title("User Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_24dp)));
                    mMap.setMaxZoomPreference(14.0f);

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
    }




}
