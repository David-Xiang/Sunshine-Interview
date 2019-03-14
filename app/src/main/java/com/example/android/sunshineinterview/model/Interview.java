package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class Interview {
    public enum InterviewSide {
        TEACHER,    // 考官端
        STUDENT,    // 考生端
        UNKNOWN
    }
    public InterviewInfo mInterviewInfo;

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

    private static Interview mInterview;

    private InterviewStatus mStatus;
    private InterviewSide mSide;
    private boolean isValidated;    // next 2 items are trustworthy ONLY when isValidated == true
    private boolean sideSelected;
    private boolean orderSelected;
    private int orderIndex;
    private boolean inProgress;

    private Interview() {
        isValidated = false;
        sideSelected = false;
        orderSelected = false;
        inProgress = false;

        orderIndex = -1;

        mStatus = InterviewStatus.END;
        mSide = InterviewSide.UNKNOWN;
        mInterview = null;
        mInterviewInfo = null;
    }

    public static Interview getInstance() {
        if (mInterview == null) {
            mInterview = new Interview();
        }
        return mInterview;
    }

    public InterviewStatus getStatus() {
        // TODO
        return mStatus;
    }

    public boolean setInterviewInfo(InterviewInfo i){
        mInterviewInfo = i;
        return true;
    }

    // status 暂时不管
    public boolean setStatus(InterviewStatus status) {
        switch (status) {
            case VALIDATE: // 验证考场
                mStatus = status;
                return true;
            case CHOOSESIDE: // 选择考场
                if (!isValidated)
                    return false;
                mStatus = status;
                return true;
            case CHOOSEORDER: // 选择场次
                if (!sideSelected) // TODO: 只能是考官端？？
                    return false;
                mStatus = status;
                return true;
            case SIGNIN: // TODO：只能是学生端？
                if (!orderSelected)
                    return false;
                mStatus = status;
                return true;
            case END: // 结束面试
                if (!inProgress)
                    return false;
                mStatus = status;
                return true;
        }
        return false;
    }

    public boolean validate(ValidateActivity validateActivity, String siteId, String validateCode) {
        String parameters = "/validate?siteid=" + siteId + "&validatecode=" + validateCode;
        URL url = new NetworkUtils().buildUrl(parameters);
        new ValidateTask().execute(url, validateActivity);
        return true;
    }

    public boolean setValidated() {
        isValidated = true;
        return true;
    }

    public boolean setInProgress() {
        inProgress = true;
        return true;
    }

    public boolean setSideSelected() {
        sideSelected = true;
        return true;
    }

    public boolean newInterview() {
        isValidated = false;
        sideSelected = false;
        orderSelected = false;
        inProgress = false;
        return true;
    }

    public ArrayList<String> getPeriods() {
        ArrayList<String> mPeriods = new ArrayList<String>();
        for (Period period : mInterviewInfo.periods) {
            String periodString = period.startTime + " - " + period.endTime;
            mPeriods.add(periodString);
        }
        return mPeriods;
    }

    public boolean chooseSide(ChooseSideActivity chooseSideActivity, InterviewSide interviewFunction) {
        if (!isValidated) {
            Log.e(TAG, "Code not validated when selecting side!");
            return false;
        }

        String parameters;
        if (interviewFunction == InterviewSide.TEACHER) {
            parameters = "/side?siteid=" + mInterviewInfo.siteId + "&side=teacher";
            mSide = InterviewSide.TEACHER;
        } else if (interviewFunction == InterviewSide.STUDENT) {
            parameters = "/side?siteid=" + mInterviewInfo.siteId + "&side=student";
            mSide = InterviewSide.STUDENT;
        } else {
            return false;
        }
        URL url = new NetworkUtils().buildUrl(parameters);
        new ChooseSideTask().execute(chooseSideActivity, url);
        return true;
    }

    // 设置场次只能是考官端
    public boolean setOrder(int index) {
        orderSelected = true;
        orderIndex = index;
        return true;
    }

    // 考官选择场次
    public boolean chooseOrder(int order) {
        if (!sideSelected) {
            return false;
        } else if (order >= mInterviewInfo.periods.size()) {
            return false;
        } else if (mSide != InterviewSide.TEACHER) {
            return false;
        }
        String parameters;
        if (order < 10) {
            parameters = "/order?siteid=" + mInterviewInfo.siteId + "&order=0" + String.valueOf(order);
        } else {
            parameters = "/order?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(order);
        }
        URL url = new NetworkUtils().buildUrl(parameters);
        new ChooseOrderTask().execute(url);
        // setOrder(order);
        return true;
    }

    // 考官签到
    public boolean teacher(String id) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.TEACHER)
            return false;
        String parameters = "/teacher?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex) + "&id=" + id;
        URL url = new NetworkUtils().buildUrl(parameters);
        new TeacherTask().execute(url);
        return true;
    }

    // 考官动态查看考生签到情况
    public boolean queryStudent() {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.TEACHER)
            return false;
        String parameters = "/querystudent?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        new QueryStudentTask().execute(url);
        return true;
    }

    // 考官点击开始考试
    public boolean start() {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.TEACHER)
            return false;
        String parameters = "/start?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        new StartTask().execute(url);
        return true;
    }

    // 考官点击结束考试
    public boolean end() {
        if (mSide != InterviewSide.TEACHER)
            return false;
        if (!inProgress)
            return false;
        String parameters = "/end?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        new EndTask().execute(url);
        return true;
    }

    // 学生端查询场次（与考官pad同步）
    public boolean query() {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String parameters = "/query?siteid=" + mInterviewInfo.siteId;
        URL url = new NetworkUtils().buildUrl(parameters);
        new QueryTask().execute(url);
        return true;
    }

    // 考生签到
    public boolean student(String id) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String parameters = "/student?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex) + "&id=" + id;
        URL url = new NetworkUtils().buildUrl(parameters);
        new StudentTask().execute(url);
        return true;
    }

    // 学生端查询是否可以开始考试
    public boolean queryStart() {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String parameters = "/querystart?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        new QueryStartTask().execute(url);
        return true;
    }

    // 学生端查询是否已经结束考试考试
    public boolean queryEnd() {
        if (!inProgress) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String parameters = "/queryend?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        new QueryEndTask().execute(url);
        return true;
    }
}
