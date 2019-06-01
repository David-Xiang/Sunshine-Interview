package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.model.Person;
import com.example.myapplication.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TeacherInProgressActivity extends AppCompatActivity {

    private static final String TAG = "TeacherInProgress";
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    Interview mInterview;
    private MyCamera mCamera;
    private CameraPreview mPreview;
    private MyMediaRecorder mMediaRecorder;

    int[] textViewIDs = {R.id.name0, R.id.name1, R.id.name2, R.id.name3, R.id.name4};
    int[] imageViewIDs = {R.id.photo0, R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_ing);

        Chronometer cm = findViewById(R.id.chronometer);
        cm.start();

        mInterview = Interview.getInstance();

        // 显示已经签到的学生照片
        Log.d(TAG, "shit");
        ArrayList<Person> students = mInterview.getStudents();
        Log.d(TAG, students.size() + "");
        for (int i = 0; i < students.size(); i++) {
            if (mInterview.nameList.size() > i) {
                ImageView interviewerPhoto = findViewById(imageViewIDs[i]);
                TextView interviewerName = findViewById(textViewIDs[i]);

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mInterview.pathList.get(i));
                    interviewerPhoto.setImageBitmap(BitmapFactory.decodeStream(fis));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                interviewerName.setText(mInterview.nameList.get(i));
            } else {
                TextView interviewerName = findViewById(textViewIDs[i]);
                interviewerName.setText("未签到");
            }
        }

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());

        //TODO:接视频

        Button bConfirm = findViewById(R.id.end_interview);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaRecorder.stopRecord();
                mInterview.end(TeacherInProgressActivity.this);
            }
        });
    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.END);
            Intent nextStep = new Intent(TeacherInProgressActivity.this, TeacherEndActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(TeacherInProgressActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(TeacherInProgressActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
        mMediaRecorder = new MyMediaRecorder(this, mCamera.camera, mPreview.getHolder());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMediaRecorder.startRecord(null);
            }
        }, 1000);
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (mMediaRecorder.isRecording) {
            mMediaRecorder.stopRecord();
        }
        // TODO
    }
    @Override
    protected void onStop(){
        super.onStop();
        // TODO
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        if (newString.length() > 10)
        {
            String[] tmp = newString.split(" ");
            newString = tmp[1].substring(0, 8) + " - " + tmp[2];
        }
        textview.setText(originalString.replace("------", newString));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK)
            return true; //不执行父类点击事件
        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
    }
}
