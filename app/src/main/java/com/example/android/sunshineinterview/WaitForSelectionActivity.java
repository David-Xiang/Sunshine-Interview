package com.example.android.sunshineinterview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.R;

public class WaitForSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_selection);

        // need code here
        Synchronise();

        Intent next_step = new Intent(WaitForSelectionActivity.this, SigninActivity1.class);
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
