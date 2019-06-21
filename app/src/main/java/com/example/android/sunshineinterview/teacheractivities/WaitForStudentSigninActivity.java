package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.task.DownloadTask;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.example.android.sunshineinterview.utilities.TimeCount;
import com.example.myapplication.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.android.sunshineinterview.utilities.FileUtils.convertBmp;

public class WaitForStudentSigninActivity extends AppCompatActivity {
    private final static String TAG = "WaitForStudentSigninAc";
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }
    private Interview mInterview;
    private TimeCount mTimeCount;
    private ArrayList<String> mStudentNames;



    private MyCamera mCamera;
    private CameraPreview mPreview;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mManager;

    private ArrayList<TextView> mTextViewArray;
    private ArrayList<ImageView> mImageViewArray;
    //int[] textViewIDs = {R.id.name0, R.id.name1, R.id.name2, R.id.name3, R.id.name4};
    //int[] imageViewIDs = {R.id.photo0, R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4};
    // int cnt_ready = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_action);

        //mCamera = new MyCamera(this);
        //mPreview = new CameraPreview(this, mCamera.camera);
        //FrameLayout preview = findViewById(R.id.videoView);
        //preview.addView(mPreview);

        mInterview = Interview.getInstance();

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_time_text, R.string.interview_time_text, mInterview.getInterviewTime());
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());

        mTimeCount = new TimeCount(180000, 2000){
            @Override
            public void onTick(long millisUntilFinished) {
                if (count > 0)
                    mInterview.queryStudent(WaitForStudentSigninActivity.this);
                count++;
            }
        };
        mTimeCount.start();


        mStudentNames = mInterview.getStudentNames();
        mRecyclerView = findViewById(R.id.recyclerView);
        mManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new RecyclerViewAdapter(mStudentNames);
        mRecyclerView.setAdapter(mAdapter);
        mTextViewArray = new ArrayList<>();
        mImageViewArray = new ArrayList<>();

//        int i = 0;
//        for(String s:mStudentNames) {
//            Log.v(TAG, s);
//            updateNames(i, s);
//            i += 1;
//        }
               
        Button bConfirm = findViewById(R.id.manual_start);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send start information
                ProgressBar pb_validate = findViewById(R.id.pb_start);
                pb_validate.setVisibility(View.VISIBLE);
                mInterview.start(WaitForStudentSigninActivity.this);
            }
        });

        Button bReady = findViewById(R.id.button_ready_start2);
        bReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "confirm clicked");
                Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
                startActivity(nextStep);
            }
        });
    }

    private void updateNames(int index, String studentName){
        TextView textview = mTextViewArray.get(index);
        textview.setText(studentName);
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
    protected void onDestroy() {
        super.onDestroy();
        mTimeCount.cancel();
    }

    public void onStudentsUpdate(String name, String url) {
        Log.d(TAG, "update student @!!!!!!!!!!!!!!!!!!!!!!!!");
        //一个一个在mInterview里面修改/信息，name, url, isabsent
        for (int j = 0; j < mStudentNames.size(); ++j)
        {
            if(name.equals(mStudentNames.get(j)) && url != null) {

                // 保存每个学生的照片位置。
                mInterview.addNameAndPath(name, url);

                ImageView interviewerPhoto = mImageViewArray.get(j);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(url);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    Bitmap btp = BitmapFactory.decodeStream(fis, null, options);
                    interviewerPhoto.setImageBitmap(convertBmp(btp));
                    fis.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                mTextViewArray.get(j).setText(name);

                break;
            }
        }
    }

    public void onHttpResponse(ServerInfo serverInfo){
        ProgressBar pb_validate = findViewById(R.id.pb_start);
        pb_validate.setVisibility(View.GONE);
        if (serverInfo == ServerInfo.PERMISSION){
            mTimeCount.cancel();
            mInterview.setStatus(Interview.InterviewStatus.INPROGRESS);
            Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ServerInfo.REJECTION) {
            Toast.makeText(WaitForStudentSigninActivity.this, "客户端冲突，请重新选择", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WaitForStudentSigninActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mCamera = new MyCamera(this);
        mPreview = new CameraPreview(this, mCamera.camera);
        FrameLayout preview = findViewById(R.id.videoView);
        preview.addView(mPreview);
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
                mTextViewArray.add(name);
                mImageViewArray.add(avatar);
            }
        }

        private ArrayList<String> names;
        private final static String TAG = "RecyclerViewAdapter";

        public RecyclerViewAdapter(ArrayList<String> names){
            this.names = names;
        }

        @Override
        public RecyclerViewAdapter.VH onCreateViewHolder(ViewGroup viewGroup, int i) {
            //LayoutInflater.from指定写法
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.protraits, viewGroup, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.VH vh, int i) {
            Log.i(TAG, "getItemCount: i = " + i);
            Log.i(TAG, "getItemCount: name = " + names.get(i));
            vh.name.setText(names.get(i));
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "getItemCount: names.size() = " + names.size());
            return names.size();
        }
    }
}
