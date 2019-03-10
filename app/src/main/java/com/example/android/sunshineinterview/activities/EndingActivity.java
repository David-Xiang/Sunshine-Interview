package com.example.android.sunshineinterview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class EndingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);

        Button b_confirm = findViewById(R.id.button_return);
        b_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next_step = new Intent(EndingActivity.this, SelectActivity.class);
                startActivity(next_step);
            }
        });
    }
}
