package com.example.android.sunshineinterview.studentactivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.task.TimeTask;
import com.example.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class WaitForChooseOrderActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private static final int TIMER = 999;
    private TimeTask mTask;
    private Handler mHandler;
    private Interview mInterview;

    private MyCamera mCamera;
    private CameraPreview mPreview;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_selection);

        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);

        // TODO: query

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case TIMER:
                        //在此执行定时操作
                        mInterview.query(WaitForChooseOrderActivity.this);
                        break;
                    default:
                        break;
                }
            }
        };

        mTask = new TimeTask(1000, new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(TIMER);
                //或者发广播，启动服务都是可以的
            }
        });
        mTask.start();
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    private void stopTimer(){
        mTask.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    public void onHttpResponse(){
        mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
        Intent nextStep = new Intent(WaitForChooseOrderActivity.this, StudentSigninActivity.class);
        startActivity(nextStep);
    }

    @Override
    protected void onResume(){
        super.onResume();
        // TODO
    }
    @Override
    protected void onPause(){
        super.onPause();
        // TODO
    }
    @Override
    protected void onStop(){
        super.onStop();
        // TODO
    }
}
