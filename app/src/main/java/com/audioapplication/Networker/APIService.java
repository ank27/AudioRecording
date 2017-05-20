package com.audioapplication.Networker;

import com.audioapplication.Models.AudioResponse;
import com.audioapplication.Models.SignInResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static com.audioapplication.Networker.NetworkConfigs.POST_AUDIO;
import static com.audioapplication.Networker.NetworkConfigs.SIGN_IN;

/**
 * Created by ankurkhandelwal on 17/05/17.
 */

public interface APIService {

    @POST(SIGN_IN)
    Call<SignInResponse> signIn(@Body HashMap<String,String> request);

    @POST(POST_AUDIO)
    Call<AudioResponse> postAudio(@Body HashMap<String,String> request);
}
