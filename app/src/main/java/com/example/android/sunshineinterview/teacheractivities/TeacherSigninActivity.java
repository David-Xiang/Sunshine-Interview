package com.example.android.sunshineinterview.teacheractivities;

import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
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
import android.hardware.Camera;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.FindDir;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;
import com.example.android.sunshineinterview.model.Person;

import static com.example.android.sunshineinterview.Camera.FindDir.*;
import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_VIDEO;
import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class TeacherSigninActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;

    Interview mInterview;
    private String[] teacherNames;
    int mSigninNumber;
    private MyCamera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        mInterview = Interview.getInstance();
        teacherNames = mInterview.getTeacherNames();
        mSigninNumber = 0;

        // ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
        // mCamera = new MyCamera(this, interviewerPhoto);
        // mPreview = new CameraPreview(this, mCamera.camera);
        // FrameLayout preview = findViewById(R.id.videoView);
        // preview.addView(mPreview);

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());


        initSpinner();

        //TODO: 拍照
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);
        Button bConfirm = findViewById(R.id.button_confirm);

        bShoot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO 判断有没有选择考官（通过禁用按钮）
//                Log.d("mydebug", "start taking picture");
                // mCamera.takePhoto();
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
                    imageUri= FileProvider.getUriForFile(TeacherSigninActivity.this,
                            "com.example.android.sunshineinterview.fileprovider",outputImage);
                }else {
                    imageUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 2);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        bReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO 重置imageView
                ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
                interviewerPhoto.setImageResource(R.drawable.bigbrother);
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);
                // TODO: time
                //String time = sp.getSelectedItem().toString();
                // TODO(XIEXINTONG): 把图片地址填上
                String imgpath = new String();
                mInterview.teacherSignin(TeacherSigninActivity.this,
                        sp.getSelectedItemPosition(), imgpath);
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

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
    }

    // 监听Activity状态
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("mydebug", "onResume if called");
        ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
        mCamera = new MyCamera(this, interviewerPhoto);
        mCamera.setCameraDisplayOrientation(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
        // TODO
    }
    @Override
    protected void onPause(){
        Log.d("mydebug", "TeacherSignin onPause called");
        super.onPause();
        if (mCamera.AcquireCamera() == null){
            Log.d("mydebug", "after pausing, camera released!");
        }
        // TODO
    }
    @Override
    protected void onStop(){
        super.onStop();
        // TODO
    }
}
