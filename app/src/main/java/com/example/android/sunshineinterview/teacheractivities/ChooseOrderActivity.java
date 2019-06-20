package com.example.android.sunshineinterview.teacheractivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.Camera.CameraPreview;
import com.example.android.sunshineinterview.Camera.MyCamera;
import com.example.android.sunshineinterview.Camera.MyMediaRecorder;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.model.Interview;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;

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

    private boolean signinNeeded;

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

        final Button bConfirm = findViewById(R.id.confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = findViewById(R.id.spinner);
                // TODO: time?
                String time = sp.getSelectedItem().toString();

                // reset videoID
                new MyMediaRecorder();

                mInterview.setOrder(OrderIDs.get(sp.getSelectedItemPosition()));
                CheckBox ad = findViewById(R.id.checkBox);
                mInterview.setSigninSkipped(ad.isChecked());
                mInterview.updatePersonInfo();
                showListDialog();
            }
        });

        CheckBox cb = findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    bConfirm.setText("开始面试");
                    bConfirm.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                else{
                    bConfirm.setText("确认");
                    bConfirm.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    public void onHttpResponse(ChooseOrderActivity.ServerInfo serverInfo)
    {
        if (serverInfo == ChooseOrderActivity.ServerInfo.PERMISSION){
            periodAdapter.remove(sp.getSelectedItem().toString());
            OrderIDs.remove(sp.getSelectedItemPosition());
            if (mInterview.isSigninSkipped())
            {
                mInterview.setStatus(Interview.InterviewStatus.INPROGRESS);
                Intent nextStep = new Intent(ChooseOrderActivity.this, TeacherInProgressActivity.class);
                startActivity(nextStep);
            }
            else
            {
                mInterview.setStatus(Interview.InterviewStatus.SIGNIN);
                Intent nextStep = new Intent(ChooseOrderActivity.this, TeacherSigninActivity.class);
                startActivity(nextStep);
            }
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

    private void showListDialog(){
        ArrayList<String> listItems = new ArrayList<>(Arrays.asList("考试时间：" + sp.getSelectedItem().toString(),"是否需要签到：" + (mInterview.isSigninSkipped()?"否":"是"),"参与面试人员姓名："));
        for(int i = 0; i < mInterview.getStudentNames().size(); ++i)
        {
            listItems.add(mInterview.getStudentNames().get(i));
        }

        final AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle(getString(R.string.dialog_list_text));
        listDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        设置item 不能用setMessage()
        用setItems
        items : listItems[] -> 列表项数组
        listener -> 回调接口
    */
        listDialog.setItems(listItems.toArray(new String[listItems.size()]),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;
            }
        });

        //设置按钮
        listDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
                , new DialogInterface.OnClickListener() {
                    @Override
                    // TODO: 状态修改与同步
                    public void onClick(DialogInterface dialog, int which) {
                        Spinner sp = findViewById(R.id.spinner);
                        mInterview.chooseOrder(ChooseOrderActivity.this, OrderIDs.get(sp.getSelectedItemPosition()));
                    }
                });

        listDialog.setNegativeButton(getString(R.string.dialog_btn_cancel_text)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        listDialog.create().show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mPeriods.isEmpty()) {
            Intent nextStep = new Intent(ChooseOrderActivity.this, ValidateActivity.class);
            startActivity(nextStep);
        }
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
