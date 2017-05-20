package com.audioapplication.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MarshMallowPermission extends Activity {
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE=2;
    public static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 3;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    Activity activity;
    public String TAG = "MarshMallowPermission";
    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }

    /**
     * check all permissions at app startup
     * */
    public void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Read Phone State");
        if(!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE ))
            permissionsNeeded.add("Access External Storage");
        if(!addPermission(permissionsList,Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Record Audio");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant permission to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity,permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            ActivityCompat.requestPermissions(activity,permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity).setMessage(message).setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null).create().show();
    }

    /**
     * chek permission for marshmallow devices to Access External storage
     * @return
     */
    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * chek permission for marshmallow devices to Record Audio
     * @return
     */
    public boolean checkPermissionForRecordAudio(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }


    /**
     * chek permission for marshmallow devices to Read Phone State
     * @return
     */
    public boolean checkPermissionForReadPhoneState(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /**
     * chek permission for marshmallow devices to Read Phone State
     * @return
     */
    public boolean checkPermissionForReadContact(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    

    /**
     * request permission for access external storage
     */
    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }
    /**
     * request permission for read phone state
     */
    public void requestPermissionForReadPhoneState(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)){
//            Toast.makeText(activity, "Read Phone state permission allow us to verify unique user.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * callback from request premission dialog
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "External Storage Permission granted");
                } else {
                    Log.d(TAG,"External Storage Permission  Permission Denied");
                }
                break;
            
            case READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Phone State Permission granted");
                } else {
                    Log.d(TAG,"Phone State Permission Denied");
                }
                break;
            case RECORD_AUDIO_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Record Audio Permission granted");
                } else {
                    Log.d(TAG,"Phone State Permission Denied");
                }
                break;
        }
    }
}
