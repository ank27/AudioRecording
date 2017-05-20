package com.audioapplication.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.audioapplication.Adapter.AudioAdapter;
import com.audioapplication.AudioApplication;
import com.audioapplication.Models.AudioPayload;
import com.audioapplication.Networker.NetworkEvent;
import com.audioapplication.Networker.Networker;
import com.audioapplication.R;
import com.audioapplication.Utils.MarshMallowPermission;
import com.audioapplication.Utils.Toaster;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class AudioFragment extends Fragment {
    Activity activity;
    View rootView;
    RecyclerView audio_container;
    RelativeLayout no_audio_layout,audio_layout,progress_layout;
    Button record_audio_btn;
    MarshMallowPermission marshMallowPermission;
    String audioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String randomAudioFileName = "ABCDEFGHIJKLMNOP";
    String TAG = "AudioFragment";
    boolean isMediaRecording = false;
    AudioAdapter audioAdapter;
    CountDownTimer timer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_audio, container, false);
        audio_container =(RecyclerView) rootView.findViewById(R.id.audio_container);
        no_audio_layout =(RelativeLayout) rootView.findViewById(R.id.no_audio_layout);
        audio_layout = (RelativeLayout) rootView.findViewById(R.id.audio_layout);
        progress_layout=(RelativeLayout) rootView.findViewById(R.id.progress_layout);
        record_audio_btn =(Button) rootView.findViewById(R.id.record_audio_btn);
        record_audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordDialog();
            }
        });
        marshMallowPermission = new MarshMallowPermission(activity);
        EventBus.getDefault().register(this);
        random = new Random();

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        audio_container.setLayoutManager(layoutManager);
        processAudio();
        return rootView;
    }

    private void showRecordDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View recordView = li.inflate(R.layout.dialog_record_audio, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(recordView);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        final TextView btn_record=(TextView) recordView.findViewById(R.id.btn_record);
        final TextView btn_stop=(TextView) recordView.findViewById(R.id.btn_stop);
        ImageView cancel_btn =(ImageView) recordView.findViewById(R.id.cancel_btn);
        final TextView counter_time = (TextView) recordView.findViewById(R.id.counter_time);
        btn_record.setEnabled(true);
        btn_stop.setEnabled(false);
        btn_stop.setBackground(ContextCompat.getDrawable(activity,R.drawable.button_disable));
        final boolean[] isRunning = {false};

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning[0]) {
                    timer.cancel();
                }
                if (isMediaRecording) {
                    mediaRecorder.release();
                }
                alertDialog.cancel();
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marshMallowPermission.checkPermissionForExternalStorage() && marshMallowPermission.checkPermissionForRecordAudio()) {
                    audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.wav";
                    Log.d(TAG,"AudioPath = "+audioSavePathInDevice);
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        isMediaRecording=true;
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    timer = new CountDownTimer(30000,100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            isRunning[0] =true;
                            long ms = millisUntilFinished;
                            String second_text = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                            String countingtext = second_text+" sec remaining";
                            counter_time.setText(countingtext);
                        }
                        @Override
                        public void onFinish() {
                            isRunning[0]=false;
                            isMediaRecording=false;
                            btn_stop.performClick();
                        }
                    };
                    timer.start();
                    btn_record.setEnabled(false);
                    btn_record.setBackground(ContextCompat.getDrawable(activity,R.drawable.button_disable));
                    btn_stop.setEnabled(true);
                    btn_stop.setBackground(ContextCompat.getDrawable(activity,R.drawable.button_shape_fill));
                    Toaster.showToast("Recording start");
                }else {
                    marshMallowPermission.checkPermissions();
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning[0]) {
                    timer.cancel();
                }
                isMediaRecording=false;
                mediaRecorder.stop();
                alertDialog.cancel();
                sendAudioData();
            }
        });
        alertDialog.show();
    }

    private void sendAudioData() {
        no_audio_layout.setVisibility(View.GONE);
        audio_layout.setVisibility(View.GONE);
        progress_layout.setVisibility(View.VISIBLE);
        final File file = new File(audioSavePathInDevice);
        Networker.getInstance().postAudio(file);
    }

    private void MediaRecorderReady() {
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(randomAudioFileName.charAt(random.nextInt(randomAudioFileName.length())));
            i++ ;
        }
        return stringBuilder.toString();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("post_audio")) {
            if (event.status) {
                Log.i(TAG, "post_audio Success");
                processAudio();
            } else {
                Log.i(TAG, "post_audio failed");
                Toaster.showToast("Couldn't upload audio...try again later!!!");
                processAudio();
            }
        }
    }

    private void processAudio() {
        Realm realm = Realm.getDefaultInstance();
        String user_id = AudioApplication.data.loadData("user_id");
        List<AudioPayload> payloads= realm.where(AudioPayload.class).equalTo("user",user_id).findAll();
        progress_layout.setVisibility(View.GONE);
        if (payloads.size()>0) {
            no_audio_layout.setVisibility(View.GONE);
            audio_layout.setVisibility(View.VISIBLE);
            audioAdapter = new AudioAdapter(payloads, activity);
            audio_container.setAdapter(audioAdapter);
        }else {
            no_audio_layout.setVisibility(View.VISIBLE);
            audio_layout.setVisibility(View.GONE);
        }
    }
}
