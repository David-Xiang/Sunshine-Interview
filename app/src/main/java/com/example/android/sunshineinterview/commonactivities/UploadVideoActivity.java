package com.example.android.sunshineinterview.commonactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.task.UploadTask;
import com.example.myapplication.R;

import java.util.ArrayList;

public class UploadVideoActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private static final String TAG = "UploadVideoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_upload);

        updateInfo(R.id.videoInfo, R.string.video_info);

        Button bConfirm = findViewById(R.id.confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar pb = findViewById(R.id.progressBar);
                pb.setVisibility(View.VISIBLE);
                TextView textview = findViewById(R.id.text_uploading);
                textview.setVisibility(View.VISIBLE);

                new UploadTask().execute("1");

            }
        });
    }

    private void updateInfo(int textViewId, int originalStringId){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);

        // TODO: get video numbers
//        int num = getVideoNumbers();
//
//        if (num == 0) {
//            textview.setText("本地文件已全部上传完毕。");
//        }
//        else {
//            String newString = Integer.toString(num);
//            textview.setText(originalString.replace("------", newString));
//        }
    }

    public void onHttpResponse(UploadVideoActivity.ServerInfo serverInfo){
        Log.v(TAG, "onHttpResponse():  method entered!");
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        if (serverInfo == UploadVideoActivity.ServerInfo.PERMISSION){
            Toast.makeText(UploadVideoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

            // TODO: 删除视频？

            Intent nextStep = new Intent(UploadVideoActivity.this, UploadMainActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == UploadVideoActivity.ServerInfo.REJECTION) {
            // do nothing?
            //Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(UploadVideoActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK)
            return true; //不执行父类点击事件
        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
    }
}
