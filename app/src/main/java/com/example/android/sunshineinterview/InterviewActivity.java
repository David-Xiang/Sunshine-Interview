package com.example.android.sunshineinterview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myapplication.R;

public class InterviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_ing);

        Button b_confirm = findViewById(R.id.end_interview);
        b_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent next_step = new Intent(InterviewActivity.this, EndingActivity.class);
                startActivity(next_step);

            }
        });
    }
}
