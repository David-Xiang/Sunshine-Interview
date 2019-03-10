package com.example.android.sunshineinterview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b_confirm = (Button) findViewById(R.id.confirm);
        b_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String room_id = ((EditText) findViewById(R.id.editText4)).getText().toString();
                String veri_code = ((EditText) findViewById(R.id.editText5)).getText().toString();
                int classroom_id = validClassroom(room_id, veri_code);
                if (classroom_id >= 0)
                {
                    Intent next_step = new Intent(MainActivity.this, ChooseSideActivity.class);
                    next_step.putExtra("classroom_id", classroom_id);
                    startActivity(next_step);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "考场号／验证码有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //    need code here
    protected int validClassroom(String room_id, String verifi_code)
    {
        if(room_id.equals("0000") && verifi_code.equals("0000"))
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
}

