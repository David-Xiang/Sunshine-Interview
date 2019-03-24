package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

public class TeacherEndActivity extends AppCompatActivity {

    Interview mInterview;
    private MyCamera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());

        Button bConfirm = findViewById(R.id.button_return);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStep = new Intent(TeacherEndActivity.this, ChooseOrderActivity.class);
                startActivity(nextStep);
            }
        });
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
    protected void onResume(){
        super.onResume();
        mCamera = new MyCamera(this);
        mCamera.setCameraDisplayOrientation(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
    }

}
