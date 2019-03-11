package com.example.android.sunshineinterview.model;

import android.app.PendingIntent;
import android.util.Log;

public class Interview {
    public enum InterviewSide{
        TEACHER,    // 考官端
        STUDENT,    // 考生端
        UNKNOWN
    }

    public enum InterviewStatus {
        VALIDATE,
        CHOOSESIDE,
        CHOOSEORDER,
        SIGNIN,
        READY,
        INPROGRESS,
        END
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
    public int mSiteId;
    public String mSchoolName;
    public String mSiteName;

    private boolean sideSelected;
    private InterviewSide mSide;
    private String [] mPeriods;

    private boolean orderSelected;
    private int orderIndex;
    private Person [] mTeachers;
    private Person [] mStudents;


    private Interview(){
        isValidated = false;
        sideSelected = false;
        orderSelected = false;
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
            case CHOOSEORDER:
                if (!sideSelected)
                    return false;
                mStatus = status;
                return true;
            case SIGNIN:
                if (!orderSelected)
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

    public boolean validateSite(String siteId, String validateCode) {
        // TODO: validate these string be numbers in (0, 10000), and if so, send a request to server
        if(siteId.equals("0000") && validateCode.equals("0000")) {
            isValidated = true;
            mSchoolName = "北京大学";
            mSiteName = "文史楼110";
            return true;
        } else {
            return false;
        }
    }

    public boolean chooseSide(InterviewSide interviewFunction){
        if (!isValidated){
            Log.e(TAG, "Code not validated when selecting side!");
        }

        // TODO: send interviewFunction to server and then receive a list of periods
        sideSelected = true;
        mSide = interviewFunction;

        // TODO: parse available periods from json
        mPeriods = new String []
                {"9:00 - 9:20", "9:20 - 9:40", "9:40 - 10:00", "10:00 - 10:20", "10:20 - 10:40"};

        return true;
    }

    public String[] getPeriods(){
        // TODO: getPeriods from server
        return mPeriods;
    }

    public boolean setOrder(int index){
        orderSelected = true;
        orderIndex = index;
        return true;
    }
}
