package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.studentactivities.WaitForChooseOrderActivity;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ChooseOrderActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    Interview mInterview;
    ArrayList<String> mPeriods;
    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_interview);
        mInterview = Interview.getInstance();
        mPeriods = mInterview.getPeriods();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);

        initSpinner(mPeriods);

        Button bConfirm = findViewById(R.id.confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = findViewById(R.id.spinner);
                // TODO: time?
                String time = sp.getSelectedItem().toString();
                mInterview.chooseOrder(ChooseOrderActivity.this, sp.getSelectedItemPosition());
            }
        });
    }

    public void onHttpResponse(ChooseOrderActivity.ServerInfo serverInfo){
        ProgressBar pb_validate = findViewById(R.id.pb_chooseside);
        pb_validate.setVisibility(View.GONE);

        if (serverInfo == ChooseOrderActivity.ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
            mInterview.setOrder(sp.getSelectedItemPosition());
            Intent nextStep = new Intent(ChooseOrderActivity.this, WaitForChooseOrderActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ChooseOrderActivity.ServerInfo.REJECTION) {
            Toast.makeText(ChooseOrderActivity.this, "选择考次错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ChooseOrderActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    private void initSpinner(ArrayList<String> timeArray) {
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, R.layout.item_select, timeArray);
        periodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(periodAdapter);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(ChooseOrderActivity.this, "您选择的是" + mPeriods.get(i), Toast.LENGTH_SHORT).show();
            Spinner sp = findViewById(R.id.spinner);
            sp.setSelection(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }
}
