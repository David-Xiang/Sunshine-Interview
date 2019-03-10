package com.example.android.sunshineinterview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myapplication.R;

public class ReadyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button b_confirm = findViewById(R.id.button_ready_start);
        b_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);

                String time = sp.getSelectedItem().toString();

                Intent next_step = new Intent(ReadyActivity.this, InterviewActivity.class);
                startActivity(next_step);
            }
        });
    }
}
