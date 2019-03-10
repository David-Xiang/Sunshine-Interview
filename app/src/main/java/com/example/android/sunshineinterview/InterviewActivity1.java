package com.example.android.sunshineinterview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.R;

public class InterviewActivity1 extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.interviewed);

        // need code here
        Synchronise();

        Intent next_step = new Intent(InterviewActivity1.this, EndingActivity1.class);
        startActivity(next_step);
    }

    // need code here
    private void Synchronise()
    {
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
