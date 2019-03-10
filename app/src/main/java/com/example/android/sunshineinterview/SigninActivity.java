package com.example.android.sunshineinterview;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;


public class SigninActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Intent intent = getIntent();
        final int interview_id = intent.getIntExtra("interview_id", 0);

        initSpinner();

        Button b_shoot = findViewById(R.id.button_shoot);

//        need code here
//        b_shoot.setOnClickListener(new View.OnClickListener()
//        {
//
//            @Override
//            public void onClick(View view) {
//                File outputImage = new File(getExternalCashDir(), getName() + ".jpg");
//                try
//                {
//                    if (outputImage.exists())
//                    {
//                        outputImage.delete();
//                    }
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//
//
//                Intent shoot = new Intent("android.media.action.IMAGE_CAPTURE");
//                shoot.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getName() + ".jpg"));
//                startActivityForResult(shoot, TAKE_PHOTO);
//            }
//        });

        Button b_confirm = findViewById(R.id.button_confirm);
        b_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);

                String time = sp.getSelectedItem().toString();

                Intent next_step = new Intent(SigninActivity.this, ConfirmActivity.class);
                next_step.putExtra("interview_id", interview_id);
                startActivity(next_step);
            }
        });

        Button b_reset = findViewById(R.id.button_reset);
    }

    private void initSpinner()
    {
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this, R.layout.item_select, interviewerArray);
        timeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(timeAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    // need code here
    private String[] getInterviewerArray()
    {
        return new String[]{"考官1", "考官2", "考官3", "考官4", "考官5"};
    }

    private String[] interviewerArray = getInterviewerArray();

    class MySelectedListener implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            Toast.makeText(SigninActivity.this, "您选择的是" + interviewerArray[i], Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }
}
