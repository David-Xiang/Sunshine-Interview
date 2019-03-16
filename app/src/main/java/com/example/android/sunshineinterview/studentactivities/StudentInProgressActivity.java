package com.example.android.sunshineinterview.studentactivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.task.TimeTask;
import com.example.myapplication.R;

import java.util.TimerTask;

public class StudentInProgressActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private Interview mInterview;
    private TimeTask mTask;
    private static final int TIMER = 999;
    private Handler mHandler;

    private MyCamera mCamera;
    private CameraPreview mPreview;
    private MyMediaRecorder mMediaRecorder;

    @SuppressLint("HandlerLeak")
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

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        mTask = new TimeTask(1000, new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(TIMER);
                //或者发广播，启动服务都是可以的
            }
        });
        mTask.start();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case TIMER:
                        //在此执行定时操作
                        mInterview.queryEnd(StudentInProgressActivity.this);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void stopTimer(){
        mTask.stop();
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

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
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
        stopTimer();
    }
}
