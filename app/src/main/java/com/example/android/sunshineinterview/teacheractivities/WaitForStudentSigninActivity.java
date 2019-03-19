package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.utilities.TimeCount;
import com.example.myapplication.R;

import java.util.ArrayList;

public class WaitForStudentSigninActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private Interview mInterview;
    private TimeCount mTimeCount;
    private String [] mStudentNames;

    private MyCamera mCamera;
    private CameraPreview mPreview;

    int[] textViewIDs = {R.id.name0, R.id.name1, R.id.name2, R.id.name3, R.id.name4};
    int[] imageViewIDs = {R.id.photo0, R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4};
    // int cnt_ready = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_action);

        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());

        mTimeCount = new TimeCount(60000, 10000){
            @Override
            public void onTick(long millisUntilFinished) {
                if (count > 0)
                    mInterview.queryStudent(WaitForStudentSigninActivity.this);
                count++;
            }
        };
        mTimeCount.start();


        mStudentNames = mInterview.getStudentNames();

        int i = 0;
        for(String s:mStudentNames) {
            updateNames(i, s);
            i += 1;
        }

        Button bConfirm = findViewById(R.id.manual_start);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send start information
                // TODO: add a progress bar?
                mInterview.start(WaitForStudentSigninActivity.this);
            }
        });

        Button bReady = findViewById(R.id.button_ready_start2);
        bReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
                startActivity(nextStep);
            }
        });
    }

    private void updateNames(int index, String studentName){
        int textViewId = textViewIDs[index];
        TextView textview = findViewById(textViewId);
        textview.setText(studentName);
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

    public void onStudentsUpdate(String [] names, String [] urls){
        // update students' portraits
    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mTimeCount.cancel();
            mInterview.setStatus(Interview.InterviewStatus.INPROGRESS);
            Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(WaitForStudentSigninActivity.this, "客户端冲突，请重新选择", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WaitForStudentSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
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
}
