package com.example.android.sunshineinterview.teacheractivities;

import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.studentactivities.StudentSigninActivity;
import com.example.myapplication.R;
import com.example.android.sunshineinterview.model.Person;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.android.sunshineinterview.utilities.FileUtils.convertBmp;


public class TeacherSigninActivity extends AppCompatActivity {
    private static final String TAG = "TeacherSigninActivity";
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    public static final int TAKE_PHOTO = 1;

    Interview mInterview;
    private ArrayList<String> teacherNames;
    int numTeacher;
    int mSigninNumber;
    boolean [] Signed;
    private MyCamera mCamera;
    private CameraPreview mPreview;
    private ArrayAdapter<String> teacherAdapter;

    private ArrayList<Integer> teacherIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        mInterview = Interview.getInstance();
        teacherNames = mInterview.getTeacherNames();
        numTeacher = teacherNames.size();
        teacherIDs = new ArrayList<Integer>();
        for (int i = 0; i < teacherNames.size(); ++i)
        {
            teacherIDs.add(i);
        }
        Signed = new boolean[teacherNames.size()];
        for (boolean b : Signed)
        {
            b = false;
        }
        mSigninNumber = 0;

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());


        initSpinner();

        final Button bShoot = findViewById(R.id.button_shoot);
        final Button bReset = findViewById(R.id.button_reset);
        final Button bConfirm = findViewById(R.id.button_confirm);

        bShoot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 禁用button
                bShoot.setEnabled(false);
                bReset.setEnabled(false);
                bConfirm.setEnabled(false);
                mCamera.takePhoto();
            }
        });
        bReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
                interviewerPhoto.setImageResource(R.drawable.bigbrother);
                // TODO 删除刚刚拍摄的照片
                MyCamera.LastSavedLoaction = null;
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyCamera.LastSavedLoaction == null)
                {
                    Toast.makeText(TeacherSigninActivity.this, "请先拍照签到", Toast.LENGTH_LONG).show();
                    return;
                }

                Spinner sp = findViewById(R.id.spinner);
                // TODO: time

                ProgressBar pb_validate = findViewById(R.id.pb_confirm);
                pb_validate.setVisibility(View.VISIBLE);
                //Log.v("frontend", "TeacherSignInActivity(): outputimage_path: " + outputimage_path);
                // LastSavedLocation是MyCamera类的静态变量，指向上一次保存照片的路径字符串
                mInterview.teacherSignin(TeacherSigninActivity.this,
                        teacherIDs.get(sp.getSelectedItemPosition()), MyCamera.LastSavedLoaction);
                //mInterview.teacherSignin(TeacherSigninActivity.this,
                // sp.getSelectedItemPosition(), imgpath);


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
        ProgressBar pb_validate = findViewById(R.id.pb_confirm);
        pb_validate.setVisibility(View.GONE);
        if (serverInfo == ServerInfo.PERMISSION){
            mSigninNumber++;
            Spinner sp = findViewById(R.id.spinner);
            teacherAdapter.remove(teacherAdapter.getItem(sp.getSelectedItemPosition()));
            teacherIDs.remove(sp.getSelectedItemPosition());
            ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
            interviewerPhoto.setImageResource(R.drawable.bigbrother);
            MyCamera.LastSavedLoaction = null;
            if (mSigninNumber == numTeacher){
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
        teacherAdapter = new ArrayAdapter<>(this, R.layout.item_select, teacherNames);
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
            Toast.makeText(TeacherSigninActivity.this, "您选择的是" + teacherNames.get(i), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        if (newString.length() > 10)
        {
            String[] tmp = newString.split(" ");

            newString = tmp[1].substring(0, 8) + " - " + tmp[2];
        }
        textview.setText(originalString.replace("------", newString));
    }

    public void onPhotoTaken(String filePath) {
        try {
            ImageView interviewerPhoto = findViewById(R.id.interviewer_photo);
            FileInputStream fis = new FileInputStream(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap btp = BitmapFactory.decodeStream(fis, null, options);
            interviewerPhoto.setImageBitmap(convertBmp(btp));
            fis.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        //启用button
        Button bShoot = findViewById(R.id.button_shoot);
        Button bReset = findViewById(R.id.button_reset);
        Button bConfirm = findViewById(R.id.button_confirm);
        bShoot.setEnabled(true);
        bReset.setEnabled(true);
        bConfirm.setEnabled(true);
    }

    // 监听Activity状态
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume if called");
        mCamera = new MyCamera(this, TeacherSigninActivity.this, null);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK)
            return true; //不执行父类点击事件
        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
    }
}
