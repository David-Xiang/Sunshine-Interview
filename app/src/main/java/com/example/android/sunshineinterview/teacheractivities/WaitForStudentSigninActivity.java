package com.example.android.sunshineinterview.teacheractivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.model.TimeTask;
import com.example.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class WaitForStudentSigninActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private static final int TIMER = 999;
    private TimeTask mTask;
    private Handler mHandler;
    private Interview mInterview;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_action);
        mInterview = Interview.getInstance();

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
                        mInterview.queryStudent(WaitForStudentSigninActivity.this);
                        break;
                    default:
                        break;
                }
            }
        };

        Button bConfirm = findViewById(R.id.manual_start);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send start information
                // TODO: add a progress bar?
                mInterview.start(WaitForStudentSigninActivity.this);
            }
        });
    }

    private void stopTimer(){
        mTask.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    public void onStudentsUpdate(String [] names, String [] urls){
        // update students' portraits
    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.INPROGRESS);
            Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(WaitForStudentSigninActivity.this, "客户端冲突，请重新选择", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WaitForStudentSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }


    //TODO: 实时更新照片，如果人齐了变换按钮
}
