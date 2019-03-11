package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.R;

public class StudentInProgressActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.interviewed);

        // need code here
        Synchronise();

        Intent nextStep = new Intent(StudentInProgressActivity.this, StudentEndActivity.class);
        startActivity(nextStep);
    }

    // need code here
    private void Synchronise() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
