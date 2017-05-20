package com.audioapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.audioapplication.Models.Product;
import com.audioapplication.Networker.Networker;
import com.audioapplication.Utils.ConnectionDetector;

import java.util.ArrayList;

public class Data {

    public final String TAG = "Data";
    static AudioApplication mainApp;
    private Context context;
    public static final String saveTag = "AudioDATA";
    private Networker mNetworker;
    ConnectionDetector connectionDetector;
    public static ArrayList<Product> product_list=new ArrayList<>();
    public Data(Context cont) {
        context = cont;
        mainApp = (AudioApplication) cont;
        mNetworker = Networker.getInstance();
//        mRealm = Realm.getDefaultInstance();
        //searchResult = null;
        connectionDetector=new ConnectionDetector(cont);
        mNetworker.loadJson(context);
    }

    public boolean isConnectedToInternet(){
        if(!connectionDetector.isConnectedToInternet()) {
            Log.d(TAG,"not connected to internet");
            return false;
        }else {
            Log.d(TAG,"connected to internet");
            return true;
        }
    }

    public String loadData(String name) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString(name, "");
    }

    public void saveData(String name, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(name, value).commit();
    }

    public boolean loadBooleanData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        return prefs.getBoolean(name, false);
    }

    public void saveData(String name, Boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public void saveData(String name, double value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putLong(name, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    public double loadDoubleData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(prefs.getLong(name, 0));
    }

    public void saveData(String name, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.commit();
    }


    public void saveIntData(String name, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public int loadIntData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        int restoredInt = prefs.getInt(name, 0);
        return restoredInt;
    }

    public void saveLongData(String name, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putLong(name, value);
        editor.commit();
    }

    public long loadLongData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        long restoredInt = prefs.getLong(name, 0);
        return restoredInt;
    }

    public void clear() {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.clear().apply();
    }
}
