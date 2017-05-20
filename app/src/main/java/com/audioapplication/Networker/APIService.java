package com.audioapplication.Networker;

import com.audioapplication.Models.AudioResponse;
import com.audioapplication.Models.SignInRequest;
import com.audioapplication.Models.SignInResponse;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;


import static com.audioapplication.Networker.NetworkConfigs.AUDIO_DATA;
import static com.audioapplication.Networker.NetworkConfigs.POST_AUDIO;
import static com.audioapplication.Networker.NetworkConfigs.SIGN_IN;

/**
 * Created by ankurkhandelwal on 17/05/17.
 */

public interface APIService {

    @POST(SIGN_IN)
    void signIn(@Body HashMap<String,String> request, Callback<SignInResponse> callback);

//    @Multipart
//    @POST(POST_AUDIO)
//    void postAudio(@Part("audio_data") TypedFile file, Callback<Void> avoid);

//    @POST(POST_AUDIO)
//    void postAudio(@Body HashMap<String,File> request, Callback<Void> avoid);

    @Multipart
    @POST(POST_AUDIO)
    void postAudio(@Part("audio_data\"; filename=\"myAudio.wav\" ") RequestBody file,Callback<AudioResponse> callback);

//    @GET(AUDIO_DATA)
//    void getAudio(Callback<AudioResponse> callback);
}
