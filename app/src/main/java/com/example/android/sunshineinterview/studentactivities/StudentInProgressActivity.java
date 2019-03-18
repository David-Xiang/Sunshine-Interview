package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

public class StudentInProgressActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private Interview mInterview;
    private TimeCount mTimeCount;

    private MyCamera mCamera;
    private CameraPreview mPreview;
    private MyMediaRecorder mMediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.interviewed);

        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
        mMediaRecorder = new MyMediaRecorder(this, mCamera.camera, mPreview.getHolder());

        mInterview = Interview.getInstance();

        mTimeCount = new TimeCount(60000, 10000);

    }


    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            Intent nextStep = new Intent(StudentInProgressActivity.this, StudentEndActivity.class);
            startActivity(nextStep);
        } else {
            // do nothing?
            // Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCount.cancel();
    }
    class TimeCount extends CountDownTimer {
        private int count;
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            count = 0;
        }

        @Override
        public void onFinish() {
            // TODO: no connection for a long time
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (count > 0)
                mInterview.queryEnd(StudentInProgressActivity.this);
            count++;
        }
    }
}
