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
import com.example.android.sunshineinterview.model.Person;

import java.util.ArrayList;


public class TeacherSigninActivity extends AppCompatActivity {
    Interview mInterview;
    ArrayList<Person> mTeachers;
    ArrayList<String> teacherNames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        mInterview = Interview.getInstance();

        //TODO: get老师列表，或许是一个Person类型的？
        mTeachers = mInterview.getTeachers();
        for(Person t:mTeachers)
        {
            teacherNames.add(t.name);
        }

        initSpinner(mTeachers);

        //TODO: 拍照
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);

        Button bConfirm = findViewById(R.id.button_confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);
                String name = sp.getSelectedItem().toString();
                if (mInterview.teacher(mTeachers.get(sp.getSelectedItemPosition()).id))
                {
                    teacherNames.remove(sp.getSelectedItemPosition());
                }
            }
        });

    }

    private void initSpinner(ArrayList<Person> teacherArray) {
        ArrayAdapter<Person> teacherAdapter = new ArrayAdapter<Person>(this, R.layout.item_select, teacherArray);
        teacherAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考官");
        sp.setAdapter(teacherAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }


    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(TeacherSigninActivity.this, "您选择的是" + teacherNames.get(i), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

}
