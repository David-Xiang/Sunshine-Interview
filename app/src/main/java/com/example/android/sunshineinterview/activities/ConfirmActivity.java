package com.example.android.sunshineinterview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myapplication.R;

public class ConfirmActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_action);

        Button b_confirm = findViewById(R.id.manual_start);
        b_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent next_step = new Intent(ConfirmActivity.this, InterviewActivity.class);
                startActivity(next_step);
            }
        }
        );
    }
}
