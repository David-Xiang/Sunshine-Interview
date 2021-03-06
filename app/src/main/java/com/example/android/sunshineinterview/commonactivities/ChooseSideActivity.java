package com.example.android.sunshineinterview.commonactivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.studentactivities.WaitForChooseOrderActivity;
import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.myapplication.R;

import com.example.android.sunshineinterview.model.Interview;

public class ChooseSideActivity extends AppCompatActivity {
    private static final String TAG = "ChooseSideActivity";
    Interview mInterview;
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private MyCamera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_side);

//        DisableHomeKey();

        mInterview = Interview.getInstance();
        mInterview.setStatus(Interview.InterviewStatus.CHOOSEORDER);

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());

        Button bTeacher = findViewById(R.id.button_interviewer);
        bTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar pb_validate = findViewById(R.id.pb_chooseside);
                pb_validate.setVisibility(View.VISIBLE);
                if (!mInterview.chooseSide(ChooseSideActivity.this, Interview.InterviewSide.TEACHER)){
                    pb_validate.setVisibility(View.GONE);
                    Toast.makeText(ChooseSideActivity.this, "参数错误", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button bStudent = findViewById(R.id.button_interviewee);
        bStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar pb_validate = findViewById(R.id.pb_chooseside);
                pb_validate.setVisibility(View.VISIBLE);
                if (!mInterview.chooseSide(ChooseSideActivity.this, Interview.InterviewSide.STUDENT)){
                    pb_validate.setVisibility(View.GONE);
                    Toast.makeText(ChooseSideActivity.this, "参数错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    public void onHttpResponse(ServerInfo serverInfo){
        ProgressBar pb_validate = findViewById(R.id.pb_chooseside);
        pb_validate.setVisibility(View.GONE);

        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setSideSelected();
            mInterview.setStatus(Interview.InterviewStatus.CHOOSEORDER);
            Intent nextStep = null;
            if (mInterview.getSide() == Interview.InterviewSide.STUDENT)
                nextStep = new Intent(ChooseSideActivity.this, WaitForChooseOrderActivity.class);
            else
                nextStep = new Intent(ChooseSideActivity.this, ChooseOrderActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(ChooseSideActivity.this, "客户端冲突，请重新选择", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ChooseSideActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

//    private void DisableHomeKey() {
//        this.getWindow().setFlags(0x80000000, 0x80000000);
//    }

    @Override
    protected void onResume(){
        Log.d(TAG, "chooseSide onResume called");
        super.onResume();
        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
    }
    @Override
    protected void onPause(){
        Log.d(TAG, "ChooseSide onPause called");
        super.onPause();
        // TODO
    }
    @Override
    protected void onStop(){
        super.onStop();
        // TODO
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//        if (keyCode== KeyEvent.KEYCODE_BACK)
//            return true; //不执行父类点击事件
//        if(KeyEvent.KEYCODE_HOME==keyCode)
//            return true;
//        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
//    }
}
