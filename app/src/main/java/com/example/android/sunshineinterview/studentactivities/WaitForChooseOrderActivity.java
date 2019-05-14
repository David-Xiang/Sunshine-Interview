package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.utilities.TimeCount;
import com.example.myapplication.R;


public class WaitForChooseOrderActivity extends AppCompatActivity {
    private final static String TAG = "WaitForChooseOrder";
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private Interview mInterview;
    private TimeCount mTimeCount;

    private MyCamera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_selection);

        //mCamera = new MyCamera(this);
        //mPreview = new CameraPreview(this, mCamera.camera);
        //FrameLayout preview = findViewById(R.id.videoView);
        //preview.addView(mPreview);

        mInterview = Interview.getInstance();
        new MyMediaRecorder();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());


        // TODO: query

        mTimeCount = new TimeCount(60000, 10000){
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v(TAG, "onTick() triggered!");
                if (count > 0)
                    mInterview.query(WaitForChooseOrderActivity.this);
                count ++;
            }
        };
        mTimeCount.start();
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCount.cancel();
    }

    public void onHttpResponse(ServerInfo serverInfo, String order){
        Log.v(TAG, "onHttpResponse():  method entered!");
        if (serverInfo == ServerInfo.PERMISSION){
            Log.v(TAG, "onHttpResponse(): permisssion received!");
            mTimeCount.cancel();
            mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
            mInterview.setOrder(order);
            mInterview.updatePersonInfo();
            Intent nextStep = new Intent(WaitForChooseOrderActivity.this, StudentSigninActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Log.v(TAG, "onHttpResponse(): rejection received!");
            Toast.makeText(WaitForChooseOrderActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WaitForChooseOrderActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
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

}

