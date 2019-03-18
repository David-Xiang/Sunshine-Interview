package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

public class WaitForTeacherConfirmActivity extends AppCompatActivity {
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
        setContentView(R.layout.wait_for_action);

        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);

        mTimeCount = new TimeCount(60000, 10000);
        mTimeCount.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCount.cancel();
    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.INPROGRESS);
            Intent nextStep = new Intent(WaitForTeacherConfirmActivity.this, StudentInProgressActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            // do nothing?
            //Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WaitForTeacherConfirmActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    protected void onHttpResponse(boolean isValidated){
        ProgressBar pb_wait = findViewById(R.id.pb_waitforaction);
        if (isValidated){
            pb_wait.setVisibility(View.GONE);
            Toast.makeText(WaitForTeacherConfirmActivity.this, "即将开始面试", Toast.LENGTH_LONG).show();
            Intent nextStep = new Intent(WaitForTeacherConfirmActivity.this, StudentInProgressActivity.class);
            startActivity(nextStep);
        } else {
            Toast.makeText(WaitForTeacherConfirmActivity.this, "面试开始失败", Toast.LENGTH_LONG).show();
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
                mInterview.queryStart(WaitForTeacherConfirmActivity.this);
            count ++;
        }
    }
}
