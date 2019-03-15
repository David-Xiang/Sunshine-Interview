package com.example.android.sunshineinterview.studentactivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.model.TimeTask;
import com.example.myapplication.R;

import java.util.TimerTask;

public class StudentSigninActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private Interview mInterview;
    private String[] studentsNames;
    private int mSigninNumber;
    private static final int TIMER = 999;
    private TimeTask mTask;
    private Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_1);
        mInterview = Interview.getInstance();
        studentsNames = mInterview.getStudentNames();
        mSigninNumber = 0;

        //TODO: 更新右栏信息，获得老师列表，拍照上传...
        //TODO: to continue

        initSpinner();

        //TODO: 拍照
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);

        Button bConfirm = findViewById(R.id.button_confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);
                mInterview.studentSignin(StudentSigninActivity.this, sp.getSelectedItemPosition());
                // TODO: show a progress bar
            }
        });

        mTask = new TimeTask(1000, new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(TIMER);
                //或者发广播，启动服务都是可以的
            }
        });
        mTask.start();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case TIMER:
                        //在此执行定时操作
                        mInterview.queryStart(StudentSigninActivity.this);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void stopTimer(){
        mTask.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void initSpinner() {
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this, R.layout.item_select, studentsNames);
        studentAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //TODO: 老师sigin，可能要传图片，这里传的代表老师的参数是id
        // if (mInterview.student(mStudents.get(sp.getSelectedItemPosition()).id))
        // {
        //     studentNames.remove(sp.getSelectedItemPosition());
        // }
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(studentAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(StudentSigninActivity.this, "您选择的是" + studentsNames[i], Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

    public void onStudentsUpdate(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mSigninNumber++;
            // TODO: then let another student take photo
            if(mSigninNumber == studentsNames.length){
                // students all signed in
                mInterview.setStatus(Interview.InterviewStatus.READY);
                Intent nextStep = new Intent(StudentSigninActivity.this, WaitForTeacherConfirmActivity.class);
                startActivity(nextStep);
            }
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(StudentSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.READY);
            Intent nextStep = new Intent(StudentSigninActivity.this, StudentInProgressActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            // do nothing?
            //Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(StudentSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }
}