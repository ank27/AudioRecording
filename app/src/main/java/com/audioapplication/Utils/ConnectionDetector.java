package com.audioapplication.Utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
	private Context _context;
    boolean connected=false;

    /**
     * Constructor
     * @param context
     */
    public ConnectionDetector(Context context){
        this._context = context;
    }

    /**
     * check intenet connection returns boolean
     * @return
     */
    public boolean isConnectedToInternet(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
        return connected;
    }
}
