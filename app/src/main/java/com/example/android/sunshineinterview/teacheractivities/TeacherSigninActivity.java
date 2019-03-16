package com.example.android.sunshineinterview.teacheractivities;

import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;
import com.example.android.sunshineinterview.model.Person;
import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_VIDEO;
import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class TeacherSigninActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    Interview mInterview;
    private String[] teacherNames;
    int mSigninNumber;
    private Camera mCamera;
    private CameraPreview mPreview;
    private int cameraID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        mInterview = Interview.getInstance();
        teacherNames = mInterview.getTeacherNames();
        mSigninNumber = 0;

        mCamera = getCamera();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);

        //TODO: get老师列表，或许是一个Person类型的？
        initSpinner();

        //TODO: 拍照
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);
        Button bConfirm = findViewById(R.id.button_confirm);

        bShoot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO 判断有没有选择考官（通过禁用按钮）
                Log.d("mydebug", "start taking picture");
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
        bReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO 重置imageView
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);
                // TODO: time
                //String time = sp.getSelectedItem().toString();
                mInterview.teacherSignin(TeacherSigninActivity.this, sp.getSelectedItemPosition());
                // TODO: merge
                // String name = sp.getSelectedItem().toString();
                // if (mInterview.teacher(mTeachers.get(sp.getSelectedItemPosition()).id))
                // {
                //     teacherNames.remove(sp.getSelectedItemPosition());
                // }
            }
        });

    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mSigninNumber++;
            if (mSigninNumber == teacherNames.length){
                mInterview.setStatus(Interview.InterviewStatus.READY);
                Intent nextStep = new Intent(TeacherSigninActivity.this, WaitForStudentSigninActivity.class);
                startActivity(nextStep);
            }
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(TeacherSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(TeacherSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    private void initSpinner() {
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, R.layout.item_select, teacherNames);
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
            Toast.makeText(TeacherSigninActivity.this, "您选择的是" + teacherNames[i], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

    private Camera getCamera(){
        Camera newCamera;
        try{
            newCamera = Camera.open(cameraID);
        }
        catch (Exception e)
        {
            newCamera = null;
            e.printStackTrace();
        }
        return newCamera;
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("mPictureCallback", "called!");

            File mediaFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (mediaFile == null){                                                     ////////////
                Log.d("mydebug", "failed to open the IMG file");
            }
            try{
                FileOutputStream fos = new FileOutputStream(mediaFile);
                fos.write(data);
                fos.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            // TODO 调用下一个intent，展示拍照结果。这里只能拍照一次
            mCamera.release();
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        // TODO
    }
    @Override
    public void onPause(){
        super.onPause();
        // TODO
    }
    @Override
    public void onStop(){
        super.onStop();
        // TODO
    }
}
