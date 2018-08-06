package com.hizyaz.adrianapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.adrianapp.R;
import com.hizyaz.adrianapp.models.SharedPrefManager;

public class UploadVideoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secondary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the VideosActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_help) {
            Toast.makeText(
                    getApplicationContext(),
                    "You Clicked Help Option",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        if (id == R.id.menu_logout) {
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (id == R.id.menu_settings) {
            Toast.makeText(
                    getApplicationContext(),
                    "You Clicked Settings Option",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        //here is the main place where we need to work on.
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_videos:
                Intent h = new Intent(UploadVideoActivity.this, VideosActivity.class);
                startActivity(h);
                break;
            case R.id.nav_upload:
                Intent i = new Intent(UploadVideoActivity.this, UploadVideoActivity.class);
                startActivity(i);
                break;
            case R.id.nav_profile:
                Intent g = new Intent(UploadVideoActivity.this, ProfileActivity.class);
                startActivity(g);
                break;
//            case R.id.nav_contactus:
//                Intent s = new Intent(UploadVideoActivity.this, ContactUs.class);
//                startActivity(s);
                // this is done, now let us go and intialise the home page.
                // after this lets start copying the above.
                // FOLLOW MEEEEE>>>
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
