package com.example.android.sunshineinterview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class ChooseSideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_side);

        Button b_interviewer = findViewById(R.id.button_interviewer);
        b_interviewer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent next_step = new Intent(ChooseSideActivity.this, SelectActivity.class);
                startActivity(next_step);
            }
        });

        Button b_interviewee = findViewById(R.id.button_interviewee);
        b_interviewee.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent next_step = new Intent(ChooseSideActivity.this, WaitForSelectionActivity.class);
                startActivity(next_step);
            }
        });

    }
}
