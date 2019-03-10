package com.example.android.sunshineinterview.model;

import android.app.PendingIntent;
import android.util.Log;

public class Interview {
    public enum InterviewFunction{
        INTERVIEWER,    // 考官端
        INTERVIEWEE,    // 考生端
        UNKNOWN
    }

    public enum InterviewStatus {
        VALIDATE,       // MainActivity
        CHOOSESIDE,     // ChooseSideAcitivity
        SCHEDULE,       // SelectActivity/WaitForSelectionActivity
        SIGNIN,         // SigninActivity/SigninActivity1
        READY,          // WaitForActionActivity/ConfirmActivity/ReadyActivity
        INPROGRESS,     // InterviewActivity/InterviewActivity1
        FINISHED        // EndingActivity/EndingActivity1
    }

    private static final String TAG = "Interview";

    private class Person{
        int id;
        String name;
        boolean isAbsent;
        Person(int id, String name){
            this.id = id;
            this.name = name;
            this.isAbsent = false;
        }
        void setAbsent(){
            this.isAbsent = true;
        }
    }

    private static Interview mInterview;

    private InterviewStatus mStatus;

    private boolean isValidated;    // next 2 items are trustworthy ONLY when isValidated == true
    public int mSiteCode;
    public String mSchoolName;
    public String mSiteName;

    private boolean sideSelected;
    private InterviewFunction mFunction;
    private String [] mPeriods;

    private boolean periodSelected;
    private Person [] mInterviewers;
    private Person [] mInterviewees;


    private Interview(){
        isValidated = false;
        sideSelected = false;
        periodSelected = false;
    }

    public static Interview getInstance(){
        if (mInterview == null){
            mInterview = new Interview();
        }
        return mInterview;
    }

    public boolean setStatus(InterviewStatus status){
        // TODO
        switch (status){
            case VALIDATE:
                mStatus = status;
                return true;
            case CHOOSESIDE:
                if (!isValidated)
                    return false;
                mStatus = status;
                return true;
            case SIGNIN:
                if (!sideSelected)
                    return false;
                mStatus = status;
                return true;
        }
        return false;
    }

    public InterviewStatus getStatus(){
        // TODO
        return mStatus;
    }

    public boolean validateCode(String room_id, String veri_code) {
        // TODO: validate these string be numbers in (0, 10000), and if so, send a request to server
        if(room_id.equals("0000") && veri_code.equals("0000")) {
            isValidated = true;
            mSchoolName = "北京大学";
            mSiteName = "文史楼110";
            return true;
        } else {
            return false;
        }
    }

    public boolean selectedSide(InterviewFunction interviewFunction){
        if (!isValidated){
            Log.e(TAG, "Code not validated when selecting side!");
        }

        // TODO: send interviewFunction to server and then receive a list of periods
        sideSelected = true;
        mFunction = interviewFunction;

        // TODO: parse available periods from json
        mPeriods = new String []
                {"9:00 - 9:20", "9:20 - 9:40", "9:40 - 10:00", "10:00 - 10:20", "10:20 - 10:40"};

        return true;
    }
}
