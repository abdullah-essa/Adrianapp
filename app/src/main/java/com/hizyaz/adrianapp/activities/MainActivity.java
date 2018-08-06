package com.hizyaz.adrianapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianapp.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
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

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonForgotPassword = (Button) findViewById(R.id.buttonForgotPassword);



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
        EditText editTextForgotPassEmail = (EditText) findViewById(R.id.editTextForgotPassEmail);
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
                            JSONObject obj = new JSONObject(response.toString());
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
            protected Map<String, String> getParams() throws AuthFailureError {
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
                            JSONObject obj = new JSONObject(response.toString());
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
            protected Map<String, String> getParams() throws AuthFailureError {
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
        forgetPasswordLayout = (LinearLayout) findViewById(R.id.forgetPasswordLayout);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        forgetPasswordLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private void showForgotPasswordLayout() {
        textViewForgotPassword.setVisibility(View.INVISIBLE);
        forgetPasswordLayout = (LinearLayout) findViewById(R.id.forgetPasswordLayout);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        forgetPasswordLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);
    }
}
