package com.hizyaz.adrianapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianapp.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hizyaz.adrianapp.Config.Constants;
import com.hizyaz.adrianapp.adapters.VideoAdapter;
import com.hizyaz.adrianapp.models.SharedPrefManager;
import com.hizyaz.adrianapp.models.VideoList;
import com.hizyaz.adrianapp.network.NetworkConnection;
import com.hizyaz.adrianapp.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView navigationView;
    TextView textViewResponse,username,userEmail;
    RecyclerView recyclerView;
    NetworkConnection networkConnection;

    private List<VideoList> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_videos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView0 = navigationView.getHeaderView(0);
        username = headerView0.findViewById(R.id.username);
        username.setText(SharedPrefManager.getInstance(this).getUsername());

        View headerView1 = navigationView.getHeaderView(0);
        userEmail = headerView1.findViewById(R.id.userEmail);
        userEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());

        textViewResponse = findViewById(R.id.textViewResponse);

        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //initializing the videosList
        videoList = new ArrayList<>();

        networkConnection = new NetworkConnection(getApplicationContext());
        if(networkConnection.is_Connected())
        {
            get_videos();
        }
        else
        {
            Toast.makeText(VideosActivity.this, R.string.alert_connection_problem, Toast.LENGTH_LONG).show();
            TextView textviewAlertNoConnection = findViewById(R.id.textviewAlertNoConnection);
            textviewAlertNoConnection.setVisibility(View.VISIBLE);
        }

    }

    private void startAdapter()
    {
        //creating recyclerview adapter
        VideoAdapter adapter = new VideoAdapter(VideosActivity.this, videoList);
        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    private void get_videos() {
        String user_id =  SharedPrefManager.getInstance(getApplicationContext()).getUserid();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.VIDEOS_URL + user_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonArray;
                        hideProgress();

                        try {
                            boolean success = response.getBoolean("response");
                            if (success) {
//                                Log.e("response", response.toString());
                                String videos = response.getString("videos");
                                jsonArray = new JSONArray(videos);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject respons = jsonArray.getJSONObject(i);
                                    String id = respons.getString("id");
                                    String sent = respons.getString("sent");
                                    String deliver_date = respons.getString("deliver_date");
                                    String Title = respons.getString("title");
                                    String file_name = respons.getString("file_name");
                                    String link = Constants.VIDEOS_FOLDER_PATH + file_name;
                                    videoList.add(new VideoList(id, sent, Title, deliver_date, link));

                                }

                            } else {
                                Toast.makeText(VideosActivity.this, R.string.alert_no_videos, Toast.LENGTH_LONG).show();
                                textViewResponse.setVisibility(View.VISIBLE);
                                textViewResponse.setText(R.string.alert_no_videos);
                                Log.e("error", String.valueOf(R.string.alert_no_videos));
                            }

                        } catch (JSONException e) {
                            Log.e("e", e.toString());
                            e.printStackTrace();
                            textViewResponse.setVisibility(View.VISIBLE);
                            hideProgress();
                        }
                        // Start the VideoAdapter
                        startAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                Toast.makeText(VideosActivity.this, R.string.alert_danger_fetching_videos, Toast.LENGTH_LONG).show();
                findViewById(R.id.textviewAlertNoConnection).setVisibility(View.VISIBLE);
                hideProgress();

            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        showProgress();

    }

    public void showProgress()
    {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }
    public void hideProgress()
    {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            videoList.clear();
            get_videos();
            Toast.makeText(VideosActivity.this, "Refreshing Videos List", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.menu_help) {
            try {
                InputStream inputStream = getAssets().open("help.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader BR = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder msg = new StringBuilder();
                while ((line = BR.readLine()) != null) {
                    msg.append(line + "\n");
                }
                AlertDialog.Builder build = new AlertDialog.Builder(VideosActivity.this);
                build.setTitle(R.string.help);
                build.setIcon(R.mipmap.ic_launcher);
                build.setMessage(Html.fromHtml(msg + ""));
                build.setNegativeButton(R.string.dilog_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Negative
                    }
                }).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.menu_logout) {
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
                Intent h = new Intent(VideosActivity.this, VideosActivity.class);
                startActivity(h);
                break;
            case R.id.nav_upload:
                Intent i = new Intent(VideosActivity.this, UploadVideoActivity.class);
                startActivity(i);
                break;
            case R.id.nav_profile:
                Intent g = new Intent(VideosActivity.this, ProfileActivity.class);
                startActivity(g);
                break;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
