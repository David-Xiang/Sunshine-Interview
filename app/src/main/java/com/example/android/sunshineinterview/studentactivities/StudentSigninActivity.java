package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.model.Person;
import com.example.myapplication.R;

import java.util.ArrayList;

public class StudentSigninActivity extends AppCompatActivity {
    Interview mInterview;
    ArrayList<Person> mStudents;
    ArrayList<String> studentNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_1);
        mInterview = Interview.getInstance();

        //TODO: get考生列表，或许是一个Person类型的？
        mStudents = mInterview.getStudents();
        for(Person t:mStudents)
        {
            studentNames.add(t.name);
        }

        initSpinner();

        //TODO: 拍照
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);

        Button bConfirm = findViewById(R.id.button_confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);
                String name = sp.getSelectedItem().toString();
                //TODO: 老师sigin，可能要传图片，这里传的代表老师的参数是id
                if (mInterview.student(mStudents.get(sp.getSelectedItemPosition()).id))
                {
                    studentNames.remove(sp.getSelectedItemPosition());
                }
            }
        });

    }

    private void initSpinner() {
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<String>(this, R.layout.item_select, studentNames);
        studentAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(studentAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }


    class MySelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(StudentSigninActivity.this, "您选择的是" + studentNames.get(i), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

}