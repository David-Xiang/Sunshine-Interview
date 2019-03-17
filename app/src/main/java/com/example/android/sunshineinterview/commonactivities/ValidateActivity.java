package com.example.android.sunshineinterview.commonactivities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.myapplication.R;
import com.example.android.sunshineinterview.model.Interview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ValidateActivity extends AppCompatActivity {
    private Interview mInterview;

    private MyCamera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;

    private static String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private final int mRequestCode = 100;
    List<String> mPermissionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 23) { // android 6.0才用动态权限
            initPermission();
        }

        // TODO 有一个小bug，第一次申请权限时，右下角预览会不成功。

        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);

        mInterview = Interview.getInstance();
        mInterview.setStatus(Interview.InterviewStatus.VALIDATE);

        Button bConfirm = findViewById(R.id.confirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String siteId = ((EditText) findViewById(R.id.editText4)).getText().toString();
                String validateCode = ((EditText) findViewById(R.id.editText5)).getText().toString();
                mInterview.validate(ValidateActivity.this, siteId, validateCode);
                ProgressBar pb_validate = findViewById(R.id.pb_vaildate);
                pb_validate.setVisibility(View.VISIBLE);

            }
        });
    }

    public void onHttpResponse(boolean isValidated){
        ProgressBar pb_validate = findViewById(R.id.pb_vaildate);

        pb_validate.setVisibility(View.GONE);
        if (isValidated){
            Toast.makeText(ValidateActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
            Intent nextStep = new Intent(ValidateActivity.this, ChooseSideActivity.class);
            startActivity(nextStep);
        } else {
            Toast.makeText(ValidateActivity.this, "考场号／验证码有误", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPermission(){  // 申请相机、读写内存、录音等权限
        mPermissionList.clear();

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.size() > 0){
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (mRequestCode == requestCode)
        {
            for (int i = 0; i < grantResults.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    hasAllPermission = false;
            }
            if (!hasAllPermission){  // 有权限尚未获取
                Log.d("mydebug", "You have denied some permission request!");
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("mydebug", "onResume called");
        /*if (mCamera.camera == null){
            Log.d("mydebug", "onResume if called");
            mCamera.reGetCamera();
            mPreview = new CameraPreview(this, mCamera.camera);
            preview.addView(mPreview);
        }*/
    }
    @Override
    protected void onPause(){
        Log.d("mydebug", "onPause called");
        super.onPause();
        /*if (mCamera.camera != null){
            mCamera.camera.stopPreview();
            mCamera.camera.setPreviewCallback(null);
            mCamera.releaseCamera();
            preview.removeView(mPreview);
            mPreview = null;
        }*/
    }
    @Override
    protected void onStop(){
        super.onStop();
        // TODO
    }
}

