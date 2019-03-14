package com.example.android.sunshineinterview.commonactivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.studentactivities.WaitForChooseOrderActivity;
import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.myapplication.R;

import com.example.android.sunshineinterview.model.Interview;

public class ChooseSideActivity extends AppCompatActivity {
    Interview mInterview;
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_side);

        mInterview = Interview.getInstance();
        mInterview.setStatus(Interview.InterviewStatus.VALIDATE);

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        String siteId = String.format("%04d", mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);


        Button bTeacher = findViewById(R.id.button_interviewer);
        bTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar pb_validate = (ProgressBar) findViewById(R.id.pb_chooseside);
                pb_validate.setVisibility(View.VISIBLE);
                if (!mInterview.chooseSide(ChooseSideActivity.this, Interview.InterviewSide.TEACHER)){
                    pb_validate.setVisibility(View.GONE);
                    Toast.makeText(ChooseSideActivity.this, "参数错误", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        Button bStudent = findViewById(R.id.button_interviewee);
        bStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar pb_validate = (ProgressBar) findViewById(R.id.pb_chooseside);
                pb_validate.setVisibility(View.VISIBLE);
                if (!mInterview.chooseSide(ChooseSideActivity.this, Interview.InterviewSide.STUDENT)){
                    pb_validate.setVisibility(View.GONE);
                    Toast.makeText(ChooseSideActivity.this, "参数错误", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = (TextView) findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    public void onHttpResponse(ServerInfo serverInfo){
        ProgressBar pb_validate = (ProgressBar) findViewById(R.id.pb_chooseside);
        pb_validate.setVisibility(View.GONE);

        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
            Intent nextStep = new Intent(ChooseSideActivity.this, WaitForChooseOrderActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(ChooseSideActivity.this, "客户端冲突，请重新选择", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ChooseSideActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }
}
