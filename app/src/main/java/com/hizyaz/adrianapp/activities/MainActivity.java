package com.hizyaz.adrianapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.hizyaz.adrianapp.utils.GlobalVariables;
import com.hizyaz.adrianapp.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GlobalVariables globalv;
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin,buttonForgotPassword;
    private TextView textViewRegister,textViewForgotPassword,textViewLogin;
    private ProgressDialog progressDialog;
    private String username,password,email;
    private LinearLayout forgetPasswordLayout,loginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        globalv = (GlobalVariables) getApplicationContext();
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            finish();
            return;
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonLogin.setOnClickListener(this);
        buttonForgotPassword.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);


    }
    private boolean validateLoginInputs() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }
        return true;
    }
    private boolean validateForgotPasswordInput() {
        EditText editTextForgotPassEmail = findViewById(R.id.editTextForgotPassEmail);
        email = editTextForgotPassEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextForgotPassEmail.setError("Your Valid Email is required");
            return false;
        }
        return true;
    }

    private void userLogin(){

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            //new String(response.data)
//                            Log.e("USER DATA", response);
//                            AuthVars obj2 = new Gson().fromJson(response, AuthVars.class);
//                            Log.e("USER DATA Gson", obj2.toString());
//                            Log.e("ID", String.valueOf(obj2.getId()));
//                            Log.e("Username", obj2.getUsername());
//                            Log.e("Email", obj2.getEmail());
                            JSONObject obj = new JSONObject(response);
//                            Log.e("USER DATA", obj.toString());

                            if(obj.getBoolean("response")){
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("id"),
                                                obj.getString("username"),
                                                obj.getString("fullname"),
                                                obj.getString("contact"),
                                                obj.getString("email")
                                        );
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                finish();
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void sendPassword(){

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.FORGOT_PASSWORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(
                                    getApplicationContext(),
                                    obj.getString("message"),
                                    Toast.LENGTH_LONG
                            ).show();
                            if(obj.getBoolean("response")){
                                showLoginLayout();
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
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }

        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            if (validateLoginInputs())
            {
                userLogin();
            }
        }
        if (view == textViewRegister)
        {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }
        if (view == textViewForgotPassword)
        {
            showForgotPasswordLayout();
        }
        if (view == textViewLogin)
        {
            showLoginLayout();
        }
        if (view == buttonForgotPassword)
        {
            if (validateForgotPasswordInput())
                sendPassword();
        }
    }

    private void showLoginLayout() {
        textViewForgotPassword.setVisibility(View.VISIBLE);
        forgetPasswordLayout = findViewById(R.id.forgetPasswordLayout);
        loginLayout = findViewById(R.id.loginLayout);
        forgetPasswordLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private void showForgotPasswordLayout() {
        textViewForgotPassword.setVisibility(View.INVISIBLE);
        forgetPasswordLayout = findViewById(R.id.forgetPasswordLayout);
        loginLayout = findViewById(R.id.loginLayout);
        forgetPasswordLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);
    }
}
