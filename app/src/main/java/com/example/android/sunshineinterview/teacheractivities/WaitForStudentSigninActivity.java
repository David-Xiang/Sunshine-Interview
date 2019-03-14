package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class WaitForStudentSigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_action);

        Button bConfirm = findViewById(R.id.manual_start);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
                startActivity(nextStep);
            }
        });
    }

    //TODO: 实时更新照片，如果人齐了变换按钮
}
