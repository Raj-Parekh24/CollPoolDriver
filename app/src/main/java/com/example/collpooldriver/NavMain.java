package com.example.collpooldriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

public class NavMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_main);
    }

    public void OpenMenu(View view) {
        FinalSpace finalSpace = new FinalSpace();
        finalSpace.OpenMenu(view);
    }
}
