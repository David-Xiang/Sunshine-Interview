package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

public class WaitForTeacherConfirmActivity extends AppCompatActivity {
    Interview mInterview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_action);

        mInterview = Interview.getInstance();

//        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mSchoolName);
//        String siteId = String.format("%04d", mInterview.mSiteId);
//        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, siteId);
//        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mSiteName);


        Intent nextStep = new Intent(WaitForTeacherConfirmActivity.this, StudentInProgressActivity.class);
        startActivity(nextStep);
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = (TextView) findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    protected void onHttpResponse(boolean isValidated){
        ProgressBar pb_wait = (ProgressBar) findViewById(R.id.pb_waitforaction);
        if (isValidated){
            pb_wait.setVisibility(View.GONE);
            Toast.makeText(WaitForTeacherConfirmActivity.this, "即将开始面试", Toast.LENGTH_LONG).show();
            Intent nextStep = new Intent(WaitForTeacherConfirmActivity.this, StudentInProgressActivity.class);
            startActivity(nextStep);
        } else {
            Toast.makeText(WaitForTeacherConfirmActivity.this, "面试开始失败", Toast.LENGTH_LONG).show();
        }
    }
}
