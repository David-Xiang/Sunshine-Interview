package com.example.android.sunshineinterview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.R;

public class WaitForActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_action);

        // need code here
        Synchronise();

        Intent next_step = new Intent(WaitForActionActivity.this, InterviewActivity1.class);
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
