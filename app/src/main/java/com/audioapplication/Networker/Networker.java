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
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.RequestBody;

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
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        String mainUrl = DOMAIN_API_URL;

        //create service for retrofit api
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Content-Type", "application/json").build();
                return chain.proceed(request);
            }
        });
        builder.addInterceptor(interceptor);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(mainUrl)
                .client(client).addConverterFactory(GsonConverterFactory.create()).build();

        mApiService =  retrofit.create(APIService.class);


        //create api service for multipart data
        OkHttpClient.Builder builder_multi = new OkHttpClient().newBuilder();
        builder_multi.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Content-Type", "application/json")
                        .addHeader("token",AudioApplication.data.loadData("access_token"))
                        .build();
                return chain.proceed(request);
            }
        });
        builder_multi.addInterceptor(interceptor);
        OkHttpClient client_multi = builder_multi.build();
        Retrofit retrofit_multi = new Retrofit.Builder().baseUrl(mainUrl).client(client_multi).addConverterFactory(GsonConverterFactory.create()).build();

        myApi_service =  retrofit_multi.create(APIService.class);

    }



    public void signIn(String login_type, String user_id, String auth_token) {
        Log.d(TAG, "signIn called");
        HashMap<String, String> body = new HashMap<>();
        body.put("login_type", login_type);
        body.put("user_id", user_id);
        body.put("auth", auth_token);

        Call<SignInResponse> call = mApiService.signIn(body);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, retrofit2.Response<SignInResponse> response) {
                Log.d(TAG, "signIn success ");
                if (response.body().isSuccess()) {
                    AudioApplication.data.saveData("user_login","true");
                    if (response.body().getPayload().getEmail()!=null) {
                        AudioApplication.data.saveData("user_email", response.body().getPayload().getEmail());
                    }else {
                        AudioApplication.data.saveData("user_email", "");
                    }
                    AudioApplication.data.saveData("user_name",response.body().getPayload().getName());
                    AudioApplication.data.saveData("user_id",response.body().getPayload().getUser_id());
                    AudioApplication.data.saveData("access_token",response.body().getPayload().access_token);
                    EventBus.getDefault().post(new NetworkEvent("signin",true));
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d(TAG, "signIn error " + t.getMessage());
                EventBus.getDefault().post(new NetworkEvent("signin", false));
            }
        });
    }

    public void postAudio(File file) {
        HashMap<String,String> body = new HashMap<>();
        byte[] bytes = getByteArray(file);
        String file_str = new String(bytes);
        body.put("audio_data",file_str);
        Call<AudioResponse> responseCall = myApi_service.postAudio(body);
        responseCall.enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, retrofit2.Response<AudioResponse> response) {
                if (response.body().isSuccess()) {
                    Log.d(TAG, "response success");
                    Log.d(TAG, "response =" + response.body());
                    Log.d(TAG, "payload =" + response.body().getPayload());

                    AudioPayload payload = response.body().getPayload();
                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(payload);
                    mRealm.commitTransaction();
                    mRealm.close();
                    EventBus.getDefault().post(new NetworkEvent("post_audio", true));
                } else {
                    EventBus.getDefault().post(new NetworkEvent("post_audio", false));
                }
            }
            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
                EventBus.getDefault().post(new NetworkEvent("post_audio", false));
                Log.d(TAG,"response failed");
            }
        });
    }

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




