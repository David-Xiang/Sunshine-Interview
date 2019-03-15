package com.example.android.sunshineinterview.teacheractivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.model.Person;
import com.example.myapplication.R;

import java.util.ArrayList;

public class WaitForStudentSigninActivity extends AppCompatActivity {
    Interview mInterview;
    ArrayList<Person> mStudents;
    int[] textViewIDs = {R.id.name0, R.id.name1, R.id.name2, R.id.name3, R.id.name4};
    int[] imageViewIDs = {R.id.photo0, R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4};
    int cnt_ready = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_action);

        mStudents = mInterview.getStudents();

        int i = 0;
        for(Person s:mStudents)
        {
            updateInfo(i, s.name);
            i += 1;
        }

        Button bConfirm = findViewById(R.id.manual_start);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
                startActivity(nextStep);
            }
        });

        Button bReady = findViewById(R.id.button_ready_start2);
        bReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStep = new Intent(WaitForStudentSigninActivity.this, TeacherInProgressActivity.class);
                startActivity(nextStep);
            }
        });
    }

    private void updateInfo(int index, String studentName){
        int textViewId = textViewIDs[index];
        TextView textview = (TextView) findViewById(textViewId);
        textview.setText(studentName);
    }

    //TODO: 实时更新照片，如果人齐了变换按钮（参数为签到的学生id和照片）
//    protected void onHttpResponse(String id, image)
//    {
//        for (int i = 0; i < mStudents.size(); ++i)
//        {
//            if(id == mStudents.get(i).id)
//            {
//                ImageView imgview = (ImageView) findViewById(imageViewIDs[i]);
//                imgview.setImageResource(image);
//                cnt_ready += 1;
//                break;
//            }
//        }
//        if (cnt_ready == mStudents.size())
//        {
//            Button bReady = findViewById(R.id.button_ready_start2);
//            Button bConfirm = findViewById(R.id.manual_start);
//            bConfirm.setVisibility(View.GONE);
//            bReady.setVisibility(View.VISIBLE);
//        }
//    }
}
