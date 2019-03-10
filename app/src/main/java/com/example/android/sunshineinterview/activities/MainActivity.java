package com.example.android.sunshineinterview.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.android.sunshineinterview.model.Interview;

public class MainActivity extends AppCompatActivity {
    private Interview mInterview;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterview = Interview.getInstance();

        Button b_confirm = (Button) findViewById(R.id.confirm);
        b_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String room_id = ((EditText) findViewById(R.id.editText4)).getText().toString();
                String veri_code = ((EditText) findViewById(R.id.editText5)).getText().toString();
                if (mInterview.validateCode(room_id, veri_code) && mInterview.setStatus(Interview.InterviewStatus.CHOOSESIDE)) {
                    Intent next_step = new Intent(MainActivity.this, ChooseSideActivity.class);
                    startActivity(next_step);
                } else {
                    Toast.makeText(MainActivity.this, "考场号／验证码有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

