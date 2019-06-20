package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.utilities.TimeCount;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class StudentInProgressActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private Interview mInterview;
    private TimeCount mTimeCount;

    private MyCamera mCamera;
    private CameraPreview mPreview;
    private MyMediaRecorder mMediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.interviewed);

        mInterview = Interview.getInstance();

        mTimeCount = new TimeCount(6000000, 10000){
            @Override
            public void onTick(long millisUntilFinished) {
                if (count > 0)
                    mInterview.queryEnd(StudentInProgressActivity.this);
                count++;
            }
        };
        mTimeCount.start();

    }


    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mTimeCount.cancel();
            mInterview.setStatus(Interview.InterviewStatus.END);
            mMediaRecorder.stopRecord();
            Intent nextStep = new Intent(StudentInProgressActivity.this, StudentEndActivity.class);
            startActivity(nextStep);
        } else {
            // do nothing?
            // Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        }
    }

    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    public void showHashResult(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");//显示规则
        String date = sDateFormat.format(new java.util.Date());
        // TODO: HASH VALUE
        String HashValue = "849VDD78GE391EFE0";
        String text = "最新上链时间：" + date + "  Hash Value：" + HashValue;
        Toast t = Toast.makeText(StudentInProgressActivity.this, text, Toast.LENGTH_LONG);
        Display display = getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        int height = display.getHeight();
        // 这里给了一个1/4屏幕高度的y轴偏移量
        t.setGravity(Gravity.TOP, 0, height / 8);

        showMyToast(t, 59000);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
        mMediaRecorder = new MyMediaRecorder(this, mCamera.camera, mPreview.getHolder());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMediaRecorder.startRecord(StudentInProgressActivity.this);
            }
        }, 1000);
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (mMediaRecorder.isRecording) {
            mMediaRecorder.stopRecord();
        }
        // TODO
    }
    @Override
    protected void onStop(){
        super.onStop();
        // TODO
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCount.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK)
            return true; //不执行父类点击事件
        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
    }
}
