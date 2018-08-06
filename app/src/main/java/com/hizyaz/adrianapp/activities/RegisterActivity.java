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
import com.hizyaz.adrianapp.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextEmail, editTextPassword,editTextConfirmPassword,editTextFullname,editTextContact;
    private Button buttonRegister;
    private ProgressDialog progressDialog;
    private String username,fullname,email,contact,password,cpassword;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextFullname = (EditText) findViewById(R.id.editTextFullname);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextContact = (EditText) findViewById(R.id.editTextContact);

        textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
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
            Toast.makeText(RegisterActivity.this, "Username is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            this.editTextEmail.setError("email is required");
            Toast.makeText(RegisterActivity.this, "email is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(fullname)) {
            this.editTextFullname.setError("Fullname is required");
            Toast.makeText(RegisterActivity.this, "Fullname is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(contact)) {
            this.editTextContact.setError("contact is required");
            Toast.makeText(RegisterActivity.this, "contact is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(cpassword) && !password.isEmpty() && !cpassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password and Confirm Password must be matched", Toast.LENGTH_LONG).show();
            this.editTextPassword.setError("");
            this.editTextConfirmPassword.setError("");
            return false;
        }
        else
        {
            if (TextUtils.isEmpty(password)) {
                this.editTextPassword.setError("Password is required");
                Toast.makeText(RegisterActivity.this, "Password is required", Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(cpassword)) {
                this.editTextConfirmPassword.setError("Confirm Password is required");
                Toast.makeText(RegisterActivity.this, "Confirm Password is required", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
    private void registerUser() {
        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            Log.e("msg", obj.getString("message"));
                            String message = obj.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            if (obj.getBoolean("response"))
                            {
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            }
                            else
                            {
                                TextView textViewResponse = (TextView) findViewById(R.id.textViewResponse);
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
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
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

    @Override
    public void onClick(View view) {
        if (view == buttonRegister)
        {
            if (validateInputs())
                registerUser();
        }
        if(view == textViewLogin) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }

    }
}
