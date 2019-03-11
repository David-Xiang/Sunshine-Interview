package com.example.android.sunshineinterview.commonactivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.sunshineinterview.studentactivities.WaitForChooseOrderActivity;
import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.myapplication.R;

import com.example.android.sunshineinterview.model.Interview;

public class ChooseSideActivity extends AppCompatActivity {
    Interview mInterview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_side);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mSchoolName);
        String siteId = String.format("%04d", mInterview.mSiteId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mSiteName);


        Button bTeacher = findViewById(R.id.button_interviewer);
        bTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: maybe to show a progress bar is better?
                if (!mInterview.chooseSide(Interview.InterviewSide.TEACHER)){
                    // TODO: hint bad network connectivity
                }
                mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
                Intent nextStep = new Intent(ChooseSideActivity.this, ChooseOrderActivity.class);
                startActivity(nextStep);
            }
        });

        Button bStudent = findViewById(R.id.button_interviewee);
        bStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: maybe to show a progress bar is better?
                if (!mInterview.chooseSide(Interview.InterviewSide.STUDENT)){
                    // TODO: hint bad network connectivity
                }
                mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
                Intent nextStep = new Intent(ChooseSideActivity.this, WaitForChooseOrderActivity.class);
                startActivity(nextStep);
            }
        });

    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = (TextView) findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }
}
