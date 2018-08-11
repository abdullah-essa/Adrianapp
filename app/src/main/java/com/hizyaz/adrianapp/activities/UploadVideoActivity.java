package com.hizyaz.adrianapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.adrianapp.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.hizyaz.adrianapp.Config.Constants;
import com.hizyaz.adrianapp.models.SharedPrefManager;
import com.hizyaz.adrianapp.network.VolleyMultipartRequest;
import com.hizyaz.adrianapp.utils.VolleySingleton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.hizyaz.adrianapp.utils.AppHelper.allowedFileSize;
import static com.hizyaz.adrianapp.utils.AppHelper.isEmailValid;

public class UploadVideoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int SELECT_VIDEO = 3;
//    private ProgressDialog progressDialog;
private static final int STORAGE_PERMISSION_CODE = 123;

    //edittext for getting the tags input
    TextView textViewResponse;
    int uploaded_vids_by_user,allowed_uploaded_qty;
    private String selectedPath,title,deliver_date,email;
    private VideoView videoView;
    private EditText editTextTitle, editTextDeliveryDate, editTextEmail;
//    ProgressBar progressBar;
    Button buttonUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back_24dp);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffff8800));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));


//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

//        progressDialog = new ProgressDialog(this);
//        progressBar = new ProgressBar(this);

        //initializing views
        videoView = findViewById(R.id.videoView);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDeliveryDate = findViewById(R.id.editTextDeliveryDate);
        editTextEmail = findViewById(R.id.editTextEmail);
        textViewResponse = findViewById(R.id.textViewResponse);
        editTextDeliveryDate.setEnabled(false);
        buttonUpload = findViewById(R.id.buttonUpload);
        //checking the permission
        //if the permission is not given we will open setting to add permission
        //else app will not open
        requestStoragePermission();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                    Uri.parse("package:" + getPackageName()));
//            finish();
//            startActivity(intent);
//            return;
//        }

        //adding click listener to button
        buttonUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                title = editTextTitle.getText().toString().trim();
                deliver_date = editTextDeliveryDate.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();

                // Reset errors.
                editTextTitle.setError(null);
                editTextDeliveryDate.setError(null);
                editTextEmail.setError(null);

                if (title.isEmpty()) {
                    editTextTitle.setError("Enter a Title first");
                    editTextTitle.requestFocus();
                    return;
                }
                if (deliver_date.isEmpty()) {
                    editTextDeliveryDate.setError("Enter a Date first");
                    editTextDeliveryDate.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    editTextEmail.setError("Enter Email first");
                    editTextEmail.requestFocus();
                    return;
                }
                else if (!isEmailValid(email)) {
                    editTextEmail.setError("Enter a valid email");
                    return;
                }
                if (!is_allowed_to_upload())
                {
                    String stm = "Allowed Videos to upload ["+allowed_uploaded_qty+"] | Videos Uploaded By You ["+allowed_uploaded_qty+"]";
                    textViewResponse.setText(stm);
                    textViewResponse.setVisibility(View.VISIBLE);
                    return;
                }
                //if everything is ok we will open video chooser
                chooseVideo();
            }
        });
    }

    private boolean is_allowed_to_upload() {
        uploaded_vids_by_user = SharedPrefManager.getInstance(getApplicationContext()).getUserUploadedQty();
        allowed_uploaded_qty = SharedPrefManager.getInstance(getApplicationContext()).getAllowedQty();

        return uploaded_vids_by_user < allowed_uploaded_qty;
    }

    public void buttonPickDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                UploadVideoActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog2");
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                requestStoragePermission();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (monthOfYear+1)+"/"+(dayOfMonth)+"/"+year;
        editTextDeliveryDate.setText(date);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select mp4 Video "), SELECT_VIDEO);
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK && data != null) {

            System.out.println("SELECT_VIDEO");
            Uri selectedImageUri = data.getData();
            try
            {
                selectedPath = getPath(selectedImageUri);
                System.out.println(selectedImageUri);

                if (selectedPath != null) {
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(selectedImageUri);
                    videoView.setVideoPath(selectedPath);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    videoView.start();
                    textViewResponse.setText(selectedPath);
                    if (allowedFileSize(selectedPath))
                    {
                        uploadVideo();
                        showProgress();
                    }
                    else
                    {
                        hideProgress();
                        textViewResponse.setText(R.string.alert_bigFileSize);
                    }
                } else {
                    hideProgress();

                    textViewResponse.setText(R.string.alert_no_file_selected);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                hideProgress();
            }

        }
    }
    private void uploadVideo() {
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.UPLOAD_VIDEO_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideProgress();
                        try {
                            SharedPrefManager.getInstance(UploadVideoActivity.this).updateUploadedVideosByUser("+");
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UploadVideoActivity.this, VideosActivity.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse", "VolleyError");
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = getString(R.string.alert_unkown_error);
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = getString(R.string.alert_request_timeout);
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = getString(R.string.alert_failed_connection_to_server);
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
                                    errorMessage = getString(R.string.alert_resource_not_found);
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + getString(R.string.stm_login_again);
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + getString(R.string.stm_check_your_inputs);
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + getString(R.string.stm_its_getting_wrong);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        hideProgress();
//                        progressDialog.dismiss();
                        Toast.makeText(
                                UploadVideoActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                        error.printStackTrace();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", SharedPrefManager.getInstance(getApplicationContext()).getUserid());
                params.put("title", title);
                params.put("deliver_date", deliver_date);
                params.put("emails", email);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() throws IOException {
                Map<String, DataPart> params = new HashMap<>();
                long videoname = System.currentTimeMillis();
                byte [] bytes = aaa(selectedPath);
                params.put("myFile", new DataPart(videoname + ".mp4", bytes));

                return params;
            }
        };
        //10000 is the time in milliseconds adn is equal to 10 sec
//        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                20000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding the request to volley
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(volleyMultipartRequest);
        textViewResponse.setVisibility(View.INVISIBLE);

    }
    public void showProgress()
    {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }
    public void hideProgress()
    {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }
    public byte[] aaa(String filepath)
    {
        byte[] soundBytes = new byte[0];
        File initialFile = new File(filepath);
        try {
            InputStream inputStream = new FileInputStream(initialFile);
//            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return soundBytes;
    }
    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
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
        if (id == R.id.home) {
//            drawer.openDrawer(GravityCompat.START);
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
                AlertDialog.Builder build = new AlertDialog.Builder(UploadVideoActivity.this);
                build.setTitle(R.string.help);
                build.setIcon(R.mipmap.ic_launcher);
                build.setMessage(Html.fromHtml(msg + ""));
                build.setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
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
}
