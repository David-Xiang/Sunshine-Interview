package com.example.android.sunshineinterview.commonactivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.android.sunshineinterview.model.Interview;

public class ValidateActivity extends AppCompatActivity {
    private Interview mInterview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterview = Interview.getInstance();

        Button bConfirm = (Button) findViewById(R.id.confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String siteId = ((EditText) findViewById(R.id.editText4)).getText().toString();
                String validateCode = ((EditText) findViewById(R.id.editText5)).getText().toString();
                if (mInterview.validateSite(siteId, validateCode) && mInterview.setStatus(Interview.InterviewStatus.CHOOSESIDE)) {
                    Intent nextStep = new Intent(ValidateActivity.this, ChooseSideActivity.class);
                    startActivity(nextStep);
                } else {
                    Toast.makeText(ValidateActivity.this, "考场号／验证码有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
