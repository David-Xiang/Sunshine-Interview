package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;

public class StudentSigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_1);
        Intent intent = getIntent();

        initSpinner();

        Button bShoot = findViewById(R.id.button_shoot);

        Button bConfirm = findViewById(R.id.button_confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);

                Intent nextStep = new Intent(StudentSigninActivity.this, WaitForTeacherConfirmActivity.class);
                startActivity(nextStep);
            }
        });

        Button bReset = findViewById(R.id.button_reset);
    }


    private void initSpinner() {
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, R.layout.item_select, studentName);
        periodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(periodAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    // need code here
    private String[] getStudentName() {
        return new String[]{"学生1", "学生2", "学生3", "学生4", "学生5"};
    }

    private String[] studentName = getStudentName();

    class MySelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(StudentSigninActivity.this, "您选择的是" + studentName[i], Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }
}