package com.example.android.sunshineinterview.teacheractivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

import java.util.ArrayList;


public class TeacherSigninActivity extends AppCompatActivity {
    Interview mInterview;
    ArrayList<String> mTeachers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        mInterview = Interview.getInstance();

        //TODO: 更新右栏信息，获得老师列表，拍照上传...
        // mTeachers = mInterview.getPeriods().get(mInterview.)
        Intent intent = getIntent();

        initSpinner();

        Button bShoot = findViewById(R.id.button_shoot);

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

        Button bConfirm = findViewById(R.id.button_confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);

                String time = sp.getSelectedItem().toString();

                Intent nextStep = new Intent(TeacherSigninActivity.this, WaitForStudentSigninActivity.class);
                startActivity(nextStep);
            }
        });

        Button bReset = findViewById(R.id.button_reset);
    }

    private void initSpinner() {
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, R.layout.item_select, teacherNames);
        periodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(periodAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    // need code here
    private String[] getTeacherNames() {
        return new String[]{"考官1", "考官2", "考官3", "考官4", "考官5"};
    }

    private String[] teacherNames = getTeacherNames();

    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(TeacherSigninActivity.this, "您选择的是" + teacherNames[i], Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

}
