package com.example.android.sunshineinterview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        String siteCode = String.format("%04d", mInterview.mSiteCode);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, siteCode);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mSiteName);


        Button b_interviewer = findViewById(R.id.button_interviewer);
        b_interviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: maybe to show a progress bar is better?
                if (!mInterview.selectedSide(Interview.InterviewFunction.INTERVIEWER)){
                    // TODO: hint bad network connectivity
                }
                mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
                Intent next_step = new Intent(ChooseSideActivity.this, SelectActivity.class);
                startActivity(next_step);
            }
        });

        Button b_interviewee = findViewById(R.id.button_interviewee);
        b_interviewee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: maybe to show a progress bar is better?
                if (!mInterview.selectedSide(Interview.InterviewFunction.INTERVIEWEE)){
                    // TODO: hint bad network connectivity
                }
                mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
                Intent next_step = new Intent(ChooseSideActivity.this, WaitForSelectionActivity.class);
                startActivity(next_step);
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
