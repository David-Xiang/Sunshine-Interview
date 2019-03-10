package com.example.android.sunshineinterview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;

public class SelectActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_interview);
        initSpinner();

        Intent thisstep = getIntent();
        final int classroom_id = thisstep.getIntExtra("classroom_id", 0);

        Button b_confirm = (Button) findViewById(R.id.confirm);

        b_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Spinner sp = findViewById(R.id.spinner);

                String time = sp.getSelectedItem().toString();

                int interview_id = getInterviewId(classroom_id, time);

                Intent next_step = new Intent(SelectActivity.this, SigninActivity.class);
                next_step.putExtra("interview_id", interview_id);
                startActivity(next_step);
            }
        });
    }

    private void initSpinner()
    {
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this, R.layout.item_select, timeArray);
        timeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp = findViewById(R.id.spinner);
        sp.setPrompt("请选择考次");
        sp.setAdapter(timeAdapter);
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    private String[] timeArray = getTimeArray();

    class MySelectedListener implements OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            Toast.makeText(SelectActivity.this, "您选择的是" + timeArray[i], Toast.LENGTH_LONG).show();
            Spinner sp = findViewById(R.id.spinner);
            sp.setSelection(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    }

//    need code here
    private int getInterviewId(int classroom_id, String time)
    {
        return 1;
    }

//     need code here
    private String[] getTimeArray()
    {
        return new String[]{"9:00 - 9:20", "9:20 - 9:40", "9:40 - 10:00", "10:00 - 10:20", "10:20 - 10:40"};
    }
}
