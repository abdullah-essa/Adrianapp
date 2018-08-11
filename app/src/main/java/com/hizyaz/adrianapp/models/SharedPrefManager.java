package com.hizyaz.adrianapp.models;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private static final String SHARED_PREF_NAME = "mysharedpref12";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_USER_UPLOADED_QTY = "user_uploaded_qty";
    private static final String KEY_ALLOWED_QTY = "allowed_qty";

    private SharedPrefManager(Context context) {
        mCtx = context;

    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(int id, String username, String fullname, String contact, String email,int user_uploaded_qty,int allowed_qty){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_CONTACT, contact);
        editor.putInt(KEY_USER_UPLOADED_QTY, user_uploaded_qty);
        editor.putInt(KEY_ALLOWED_QTY, allowed_qty);

        editor.apply();

//        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
    public String getUserid(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return String.valueOf(sharedPreferences.getInt(KEY_USER_ID, 0));
//        sharedPreferences.getString(KEY_USER_ID, null);
    }

    public int getUserUploadedQty(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_UPLOADED_QTY, 0);
    }
    public int getAllowedQty(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ALLOWED_QTY, 0);
    }
    private void setKeyUserUploadedQty(int qty) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_UPLOADED_QTY, qty);
        editor.apply();
    }
    public void updateUploadedVideosByUser(String operation)
    {
        int uploaded_vids_by_user = getUserUploadedQty();
        if (operation.equals("+"))
            uploaded_vids_by_user = uploaded_vids_by_user+1;
        else if (operation.equals("-"))
            uploaded_vids_by_user = uploaded_vids_by_user-1;
        setKeyUserUploadedQty(uploaded_vids_by_user);
    }
    public void setKeyAllowedQty(int qty) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_ALLOWED_QTY, qty);
        editor.apply();
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }
    public String getUserContact(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CONTACT, null);
    }
    public String getUserFullname(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FULLNAME, null);
    }

    public void userProfile(int user_id, String username, String fullname, String contact, String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, user_id);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_CONTACT, contact);
        editor.apply();
    }

}
