package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class TeacherInProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_ing);

        //TODO:接视频

        Button bConfirm = findViewById(R.id.end_interview);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStep = new Intent(TeacherInProgressActivity.this, TeacherEndActivity.class);
                startActivity(nextStep);
            }
        });
    }
}
