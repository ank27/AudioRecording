package com.audioapplication.Networker;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.audioapplication.AudioApplication;
import com.audioapplication.MainActivity;
import com.audioapplication.Models.AudioPayload;
import com.audioapplication.Models.AudioResponse;
import com.audioapplication.Models.Product;
import com.audioapplication.Models.SignInRequest;
import com.audioapplication.Models.SignInResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

import static com.audioapplication.Networker.NetworkConfigs.DOMAIN_API_URL;

public class Networker {
    private static final String TAG = Networker.class.getSimpleName();
    private APIService mApiService, apiService_multipart, myApi_service;
    private ExecutorService mExecutorService;
    private Handler mMainHandler;
    private static Networker sInstance;
    Realm mRealm;
    public static Networker getInstance() {
        return sInstance != null ? sInstance : (sInstance = new Networker());
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private Networker() {
        Gson gson = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();

        String mainUrl = DOMAIN_API_URL;
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type","application/json");
            }
        };

        RequestInterceptor requestInterceptor_multipart= new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type","application/json");
                request.addHeader("token", AudioApplication.data.loadData("access_token"));
            }
        };

        RestAdapter rest = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(mainUrl)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();



        RestAdapter restAdapter_multipart = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(mainUrl)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor_multipart)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mApiService = rest.create(APIService.class);
        apiService_multipart = restAdapter_multipart.create(APIService.class);

        mMainHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newSingleThreadExecutor();

    }



    public void signIn(String login_type, String user_id, String auth_token) {
        Log.d(TAG, "signIn called");
        HashMap<String, String> body = new HashMap<>();
        body.put("login_type", login_type);
        body.put("user_id", user_id);
        body.put("auth", auth_token);

        mApiService.signIn(body, new Callback<SignInResponse>() {
            @Override
            public void success(SignInResponse signInResponse, Response response) {
                Log.d(TAG, "signIn success ");
                if (signInResponse.isSuccess()) {
                    AudioApplication.data.saveData("user_login","true");
                    if (signInResponse.getPayload().getEmail()!=null) {
                        AudioApplication.data.saveData("user_email", signInResponse.getPayload().getEmail());
                    }else {
                        AudioApplication.data.saveData("user_email", "");
                    }
                    AudioApplication.data.saveData("user_name",signInResponse.getPayload().getName());
                    AudioApplication.data.saveData("user_id",signInResponse.getPayload().getUser_id());
                    AudioApplication.data.saveData("access_token",signInResponse.getPayload().access_token);
                    EventBus.getDefault().post(new NetworkEvent("signin",true));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "signIn error " + error.getMessage());
                EventBus.getDefault().post(new NetworkEvent("signin", false));
            }
        });
    }

//    public void postAudio(File file) {
//        TypedFile typedFile = new TypedFile("multipart/form-data", file);
//        Log.d(TAG,"typedFile ="+typedFile.toString());
//        Log.d(TAG,"typedFile nam ="+typedFile.fileName());
//        apiService_multipart.postAudio(typedFile, new Callback<Void>() {
//            @Override
//            public void success(Void aVoid, Response response) {
//                Log.d(TAG, "postAudio success - ");
//                EventBus.getDefault().post(new NetworkEvent("post_audio",true));
//            }
//
//            @Override
//            public void failure(RetrofitError e) {
//                EventBus.getDefault().post(new NetworkEvent("post_audio",false));
//            }
//        });
//    }

    public void postAudio(File file) {
        RequestBody temp = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        apiService_multipart.postAudio(temp, new Callback<AudioResponse>() {
            @Override
            public void success(AudioResponse audioResponse, Response response) {
                Log.d(TAG, "postAudio success - ");
                if (audioResponse.isSuccess()) {
                    AudioPayload payload = audioResponse.getPayload();
                    Log.d(TAG,"audioPayload =" +payload.toString());
                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(payload);
                    mRealm.commitTransaction();
                    mRealm.close();
                    EventBus.getDefault().post(new NetworkEvent("post_audio", true));
                }else {
                    EventBus.getDefault().post(new NetworkEvent("post_audio", false));
                }
            }

            @Override
            public void failure(RetrofitError e) {
                Log.d(TAG, "postAudio failure - "+e.getMessage());
                EventBus.getDefault().post(new NetworkEvent("post_audio",false));
            }
        });
    }

//    public void getAudio() {
//        myApi_service.getAudio(new Callback<AudioResponse>() {
//            @Override
//            public void success(AudioResponse audioResponse, Response response) {
//                Log.d(TAG, "postAudio success - ");
//                if (audioResponse.isSuccess()) {
//                    AudioPayload payload = audioResponse.getPayload();
//                    Log.d(TAG,"audioPayload =" +payload.toString());
//                    mRealm = Realm.getDefaultInstance();
//                    mRealm.beginTransaction();
//                    mRealm.copyToRealmOrUpdate(payload);
//                    mRealm.commitTransaction();
//                    mRealm.close();
//                    EventBus.getDefault().post(new NetworkEvent("post_audio", true));
//                }else {
//                    EventBus.getDefault().post(new NetworkEvent("post_audio", false));
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError e) {
//                Log.d(TAG, "postAudio failure - "+e.getMessage());
//                EventBus.getDefault().post(new NetworkEvent("post_audio",false));
//            }
//        });
//    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("ProductInfo.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void loadJson(Context context) {
        try {
            JSONArray jsonArray= new JSONArray(loadJSONFromAsset(context));
            Product product;
            ArrayList<Product> products =new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productJson = jsonArray.getJSONObject(i);
                String product_name = productJson.getString("ProductName");
                String product_image = productJson.getString("ProductImage");
                String discount_info = productJson.getString("DiscountInfo");
                String used_today = productJson.getString("UsedToday");
                String added = productJson.getString("Added");
                String coupon = productJson.getString("Coupon");
                product = new Product(product_name,product_image,discount_info,used_today,added,coupon);
                products.add(product);
            }
            AudioApplication.data.product_list = products;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public byte[] getByteArray(File file) {
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytesArray;
    }
}




