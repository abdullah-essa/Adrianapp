package com.hizyaz.adrianapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class NetwokConnection {
    private Context mContext;
    public NetwokConnection(Context mContext) {
        this.mContext = mContext;
        is_Connected();
    }

    public Boolean is_Connected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }

        }
        return false;
    }
}