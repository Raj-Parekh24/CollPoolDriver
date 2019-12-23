package com.example.collpooldriver;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.DragAndDropPermissionsCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionManager=new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            if(firebaseUser.isEmailVerified()) {
                Toast.makeText(this,"Already signed in",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, FinalSpace.class));
            }
            else
            {
                Toast.makeText(MainActivity.this,"Please verify your email address",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void startLogin(View view) {
        Intent intent = new Intent(MainActivity.this, activity_login.class);
        startActivity(intent);
    }

    public void SignUpPro(View view) {
        startActivity(new Intent(MainActivity.this,SignUpPro.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions,grantResults);
    }
}
