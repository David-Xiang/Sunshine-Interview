package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.android.sunshineinterview.utilities.TimeCount;
import com.example.myapplication.R;

import java.util.ArrayList;


public class WaitForChooseOrderActivity extends AppCompatActivity {
    private final static String TAG = "WaitForChooseOrder";
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private Interview mInterview;
    private Handler handler;
    private Runnable runnable;
    ArrayList<String> mPeriods;

    private MyCamera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_selection);

        Log.d(TAG, "onCreate called");

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "polling handler");
                mInterview.query(WaitForChooseOrderActivity.this);
                polling();
            }
        };
        handler = new Handler();

        mInterview = Interview.getInstance();
        mPeriods = mInterview.getPeriods();
        MyMediaRecorder.resetVideoID();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());
    }

    private void polling() {
        Log.d(TAG, "polling started!");
        handler.postDelayed(runnable, 2000);
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    public void onHttpResponse(ServerInfo serverInfo, String order, String skip){
        Log.v(TAG, "onHttpResponse():  method entered!");
        if (serverInfo == ServerInfo.PERMISSION){
            Log.v(TAG, "onHttpResponse(): permisssion received!");
            mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
            mInterview.setOrder(order);
            mInterview.updatePersonInfo();
            handler.removeCallbacks(runnable);
            if (skip.equals("true")) {
                mInterview.setSigninSkipped(true);
                Intent nextStep = new Intent(WaitForChooseOrderActivity.this, StudentInProgressActivity.class);
                startActivity(nextStep);
            }
            else {
                mInterview.setSigninSkipped(false);
                Intent nextStep = new Intent(WaitForChooseOrderActivity.this, StudentSigninActivity.class);
                startActivity(nextStep);
            }
        } else if(serverInfo == ServerInfo.REJECTION) {
            Log.v(TAG, "onHttpResponse(): rejection received!");
            // Toast.makeText(WaitForChooseOrderActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WaitForChooseOrderActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStarted called");
        super.onStart();
        polling();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mPeriods.isEmpty()) {
            Intent nextStep = new Intent(WaitForChooseOrderActivity.this, ValidateActivity.class);
            startActivity(nextStep);
        }
        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
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
        // handler.removeCallbacks(runnable);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK)
            return true; //不执行父类点击事件
        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
    }
}

