package com.hizyaz.adrianapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianapp.R;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hizyaz.adrianapp.Config.Constants;
import com.hizyaz.adrianapp.models.SharedPrefManager;
import com.hizyaz.adrianapp.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private EditText editTextUsername, editTextEmail, editTextPassword,editTextConfirmPassword,editTextFullname,editTextContact;
    private String username,fullname,email,contact,password,cpassword;
    private ProgressDialog progressDialog;
    DrawerLayout drawer;
    Button buttonUpdate;
    NavigationView navigationView;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressDialog = new ProgressDialog(this);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextFullname = findViewById(R.id.editTextFullname);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextContact = findViewById(R.id.editTextContact);

        editTextUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        editTextEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        editTextFullname.setText(SharedPrefManager.getInstance(this).getUserFullname());
        editTextContact.setText(SharedPrefManager.getInstance(this).getUserContact());
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs())
                    updateProfile();
            }
        });
    }
    private boolean validateInputs() {
        email = editTextEmail.getText().toString().trim();
        username = editTextUsername.getText().toString().trim();
        fullname = editTextFullname.getText().toString().trim();
        contact = editTextContact.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        cpassword = editTextConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            this.editTextUsername.setError("Username is required");
            Toast.makeText(this, "Username is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            this.editTextEmail.setError("email is required");
            Toast.makeText(this, "email is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(fullname)) {
            this.editTextFullname.setError("Fullname is required");
            Toast.makeText(this, "Fullname is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(contact)) {
            this.editTextContact.setError("contact is required");
            Toast.makeText(this, "contact is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.isEmpty() || !cpassword.isEmpty()) {
            if (!password.equals(cpassword))
            {
                Toast.makeText(this, "Password and Confirm Password must be matched", Toast.LENGTH_LONG).show();
                if (TextUtils.isEmpty(password)) this.editTextPassword.setError("Password is required");
                if (TextUtils.isEmpty(cpassword)) this.editTextConfirmPassword.setError("Confirm Password is required");
                return false;
            }
        }
        return true;
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
                Intent h = new Intent(ProfileActivity.this, VideosActivity.class);
                startActivity(h);
                break;
            case R.id.nav_upload:
                Intent i = new Intent(ProfileActivity.this, UploadVideoActivity.class);
                startActivity(i);
                break;
            case R.id.nav_profile:
                Intent g = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(g);
                break;
//            case R.id.nav_contactus:
//                Intent s = new Intent(ProfileActivity.this, ContactUs.class);
//                startActivity(s);
                // this is done, now let us go and intialise the home page.
                // after this lets start copying the above.
                // FOLLOW MEEEEE>>>
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void updateProfile() {
        progressDialog.setMessage("Updating Your Profile...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.UPDATE_PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            Log.e("OBJECT",response);
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            if (obj.getBoolean("response"))
                            {
                                int user_id = Integer.parseInt(SharedPrefManager.getInstance(getApplicationContext()).getUserid());
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(user_id,username,fullname,contact,email);
                            }
                            else
                            {
                                TextView textViewResponse = findViewById(R.id.textViewResponse);
                                textViewResponse.setText(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String status = response.getString("status");
                                String message = response.getString("message");

                                Log.e("Error Status", status);
                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message+" Please login again";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message+ " Check your inputs";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message+" Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                        error.printStackTrace();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Log.e("id",SharedPrefManager.getInstance(getApplicationContext()).getUserid());
                Log.e("email",email);
                Log.e("username",username);
                Log.e("fullname",fullname);
                params.put("id", SharedPrefManager.getInstance(getApplicationContext()).getUserid());
                params.put("username", username);
                params.put("email", email);
                params.put("fname", fullname);
                params.put("contact", contact);
                params.put("password", password);
                params.put("cpassword", cpassword);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
