package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ChooseOrderActivity extends AppCompatActivity {
    public enum ServerInfo{
        PERMISSION,
        REJECTION,  // the side is already chosen
        NOACCESS    // bad network connectivity
    }

    private MyCamera mCamera;
    private CameraPreview mPreview;
    private ArrayList<Integer> OrderIDs;
    private ArrayAdapter<String> periodAdapter;

    Interview mInterview;
    ArrayList<String> mPeriods;
    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_interview);
        mInterview = Interview.getInstance();
        mPeriods = mInterview.getPeriods();
        OrderIDs = new ArrayList<Integer>();
        for (int i = 0; i < mPeriods.size(); ++i)
        {
            OrderIDs.add(i);
        }

        // mCamera = new MyCamera(this);
        // mPreview = new CameraPreview(this, mCamera.camera);
        // FrameLayout preview = findViewById(R.id.videoView);
        // preview.addView(mPreview);

        updateInfo(R.id.school_name_text, R.string.school_name_text, mInterview.mInterviewInfo.collegeName);
        updateInfo(R.id.college_id_text, R.string.college_id_text, mInterview.mInterviewInfo.collegeId);
        updateInfo(R.id.classroom_id_text, R.string.classroom_id_text, mInterview.mInterviewInfo.siteId);
        updateInfo(R.id.classroom_location_text, R.string.classroom_location_text, mInterview.mInterviewInfo.siteName);
        updateInfo(R.id.interview_status_text, R.string.interview_status_text, mInterview.getStatusString());

        initSpinner(mPeriods);

        Button bConfirm = findViewById(R.id.confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = findViewById(R.id.spinner);
                // TODO: time?
                String time = sp.getSelectedItem().toString();

                // reset videoID
                new MyMediaRecorder();

                mInterview.setOrder(OrderIDs.get(sp.getSelectedItemPosition()));
                mInterview.chooseOrder(ChooseOrderActivity.this, OrderIDs.get(sp.getSelectedItemPosition()));
            }
        });
    }

    public void onHttpResponse(ChooseOrderActivity.ServerInfo serverInfo)
    {
        if (serverInfo == ChooseOrderActivity.ServerInfo.PERMISSION){
            mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
            Spinner sp = findViewById(R.id.spinner);
            periodAdapter.remove(sp.getSelectedItem().toString());
            OrderIDs.remove(sp.getSelectedItemPosition());
            Intent nextStep = new Intent(ChooseOrderActivity.this, TeacherSigninActivity.class);
            startActivity(nextStep);
        } else if(serverInfo == ChooseOrderActivity.ServerInfo.REJECTION) {
            Toast.makeText(ChooseOrderActivity.this, "选择考次错误", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ChooseOrderActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    private void initSpinner(ArrayList<String> timeArray) {
        periodAdapter = new ArrayAdapter<>(this, R.layout.item_select, timeArray);
        periodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(periodAdapter);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(ChooseOrderActivity.this, "您选择的是" + mPeriods.get(i), Toast.LENGTH_SHORT).show();
            Spinner sp = findViewById(R.id.spinner);
            sp.setSelection(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

    private void updateInfo(int textViewId, int originalStringId, String newString){
        TextView textview = findViewById(textViewId);
        String originalString = getResources().getString(originalStringId);
        newString = newString == null ? "------" : newString;
        textview.setText(originalString.replace("------", newString));
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
        Log.d("mydebug", "ChooseOrder onPause called");
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
