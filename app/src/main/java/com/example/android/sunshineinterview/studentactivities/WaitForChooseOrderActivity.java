package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

public class WaitForChooseOrderActivity extends AppCompatActivity {
    Interview mInterview;
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_selection);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        String siteId = String.format("%04d", mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = (TextView) findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        while (!mInterview.query())
        {
            ;
        }
        Intent nextStep = new Intent(WaitForChooseOrderActivity.this, StudentSigninActivity.class);
        startActivity(nextStep);
    }
}
