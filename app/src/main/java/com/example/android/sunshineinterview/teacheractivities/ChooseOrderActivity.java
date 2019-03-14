package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ChooseOrderActivity extends AppCompatActivity {
    Interview mInterview;
    ArrayList<String> mPeriods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_interview);
        mInterview = Interview.getInstance();
        mPeriods = mInterview.getPeriods();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mSchoolName);
        String siteId = String.format("%04d", mInterview.mSiteId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mSiteName);

        initSpinner(mPeriods);

        Intent thisStep = getIntent();

        Button bConfirm = (Button) findViewById(R.id.confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);

                String time = sp.getSelectedItem().toString();
                mInterview.setOrder(sp.getSelectedItemPosition());
                Intent nextStep = new Intent(ChooseOrderActivity.this, TeacherSigninActivity.class);
                startActivity(nextStep);
            }
        });
    }

    private void initSpinner(ArrayList<String> timeArray) {
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, R.layout.item_select, timeArray);
        periodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(periodAdapter);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(ChooseOrderActivity.this, "您选择的是" + mPeriods.get(i), Toast.LENGTH_LONG).show();
            Spinner sp = findViewById(R.id.spinner);
            sp.setSelection(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = (TextView) findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }
}
