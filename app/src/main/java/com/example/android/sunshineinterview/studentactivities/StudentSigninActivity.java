package com.example.android.sunshineinterview.studentactivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.FindDir;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimerTask;

import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

public class StudentSigninActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private static final String TAG = "StudentSigninActivity";
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;

    private Interview mInterview;
    private String[] studentsNames;
    private int mSigninNumber;
    private TimeCount mTimeCount;

    private MyCamera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_1);
        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);

        studentsNames = mInterview.getStudentNames();
        mSigninNumber = 0;
        // TODO: demo mode
        // mTimeCount = new TimeCount(60000, 1000);
        mTimeCount = new TimeCount(60000, 10000);
        mTimeCount.start();

        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);

        //TODO: 更新右栏信息，获得老师列表，拍照上传...
        //TODO: to continue

        initSpinner();

        //TODO: 拍照
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);

        Button bConfirm = findViewById(R.id.button_confirm);

        bShoot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO 判断有没有选择考官（通过禁用按钮）
                // mCamera.takePhoto();
                // Log.d("mydebug", "start taking picture");
                File outputImage = new FindDir().getOutputMediaFile(MEDIA_TYPE_IMAGE);
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    else
                        outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(StudentSigninActivity.this,
                            "com.example.android.sunshineinterview.fileprovider",outputImage);
                }else {
                    imageUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        bReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO 重置imageView
                ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
                interviewerPhoto.setImageResource(R.drawable.bigbrother);            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);
                mInterview.studentSignin(StudentSigninActivity.this, sp.getSelectedItemPosition());
                // TODO: show a progress bar & 传照片
            }
        });



    }


    private void initSpinner() {
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this, R.layout.item_select, studentsNames);
        studentAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考生");
        sp.setAdapter(studentAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(StudentSigninActivity.this, "您选择的是" + studentsNames[i], Toast.LENGTH_SHORT).show();
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
        Log.v(TAG, "onHttpResponse():  method entered!");
        if (serverInfo == ServerInfo.PERMISSION){
            mTimeCount.cancel();
            mInterview.setStatus(Interview.InterviewStatus.INPROGRESS);
            Intent nextStep = new Intent(StudentSigninActivity.this, StudentInProgressActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            // do nothing?
            //Toast.makeText(StudentSigninActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(StudentSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    // 监听Activity状态
    @Override
    protected void onResume(){
        super.onResume();
        // TODO
    }
    @Override
    protected void onPause(){
        super.onPause();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
                        interviewerPhoto.setImageBitmap(bitmap);
                        mCamera = new MyCamera(this);
                        mPreview.resetCamera(mCamera);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();;
                    }
                }
                break;
            default:
                break;
        }
    }


    class TimeCount extends CountDownTimer {
        private int count;
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            count = 0;
        }

        @Override
        public void onFinish() {
            // TODO: no connection for a long time
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (count > 0)
                mInterview.queryStart(StudentSigninActivity.this);
            count++;
        }
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }
}