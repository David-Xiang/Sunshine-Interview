package com.example.android.sunshineinterview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class EndingActivity1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you_1);

        Button b_confirm = findViewById(R.id.button_ending);
        b_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next_step = new Intent(EndingActivity1.this, WaitForSelectionActivity.class);
                startActivity(next_step);
            }
        });
    }
}
