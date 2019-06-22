package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.model.Person;
import com.example.myapplication.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.android.sunshineinterview.utilities.FileUtils.convertBmp;

public class TeacherInProgressActivity extends AppCompatActivity {

    private static final String TAG = "TeacherInProgress";
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    Interview mInterview;
    private MyCamera mCamera;
    private CameraPreview mPreview;
    private MyMediaRecorder mMediaRecorder;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mManager;

    //int[] textViewIDs = {R.id.name0, R.id.name1, R.id.name2, R.id.name3, R.id.name4};
    //int[] imageViewIDs = {R.id.photo0, R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_ing);

        Chronometer cm = findViewById(R.id.chronometer);
        cm.start();

        mInterview = Interview.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView2);
        mManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mManager);
        ArrayList<Person> students = mInterview.getStudents();
        mAdapter = new RecyclerViewAdapter(students);
        mRecyclerView.setAdapter(mAdapter);

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());

        //TODO:接视频

        Button bConfirm = findViewById(R.id.end_interview);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaRecorder.stopRecord();
                mInterview.end(TeacherInProgressActivity.this);
            }
        });
    }

    public void onHttpResponse(ServerInfo serverInfo){
        if (serverInfo == ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.END);
            Intent nextStep = new Intent(TeacherInProgressActivity.this, TeacherEndActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(TeacherInProgressActivity.this, "签到错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(TeacherInProgressActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
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
                mMediaRecorder.startRecord(null);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK)
            return true; //不执行父类点击事件
        return super.onKeyDown(keyCode, event); //继续执行父类其他点击事件
    }



    protected class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VH>{
        public class VH extends RecyclerView.ViewHolder{
            public TextView name;
            public ImageView avatar;
            public VH(View v) {
                super(v);
                name = v.findViewById(R.id.tv_name);
                Log.i(TAG, "name == null: " + (name == null));
                avatar = v.findViewById(R.id.iv_avatar);
            }
        }

        private ArrayList<Person> people;
        private final static String TAG = "RecyclerViewAdapter";

        public RecyclerViewAdapter(ArrayList<Person> people){
            this.people = people;
        }

        @Override
        public RecyclerViewAdapter.VH onCreateViewHolder(ViewGroup viewGroup, int i) {
            //LayoutInflater.from指定写法
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.protraits, viewGroup, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.VH vh, int i) {
            vh.name.setText(mInterview.getStudentNames().get(i));

            ImageView interviewerPhoto = vh.avatar;
            if (mInterview.nameList != null && mInterview.pathList != null) {
                FileInputStream fis = null;
                String fileStr = mInterview.getPathFromName(mInterview.getStudentNames().get(i));
                if (fileStr.equals(""))
                    return;
                try {
                    fis = new FileInputStream(fileStr);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    Bitmap btp = BitmapFactory.decodeStream(fis, null, options);
                    interviewerPhoto.setImageBitmap(convertBmp(btp));
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "getItemCount: people.size() = " + people.size());
            return people.size();
        }
    }
}
