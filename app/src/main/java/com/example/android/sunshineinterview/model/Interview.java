package com.example.android.sunshineinterview.model;

import android.app.PendingIntent;
import android.util.Log;

import com.example.android.sunshineinterview.utilities.NetworkUtils;

import java.net.URL;

public class Interview {
    public enum InterviewSide {
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

    private class Person {
        int id;
        String name;
        boolean isAbsent;

        Person(int id, String name) {
            this.id = id;
            this.name = name;
            this.isAbsent = false;
        }

        void setAbsent() {
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
    private String[] mPeriods;

    private boolean orderSelected;
    private int orderIndex;
    private Person[] mTeachers;
    private Person[] mStudents;

    private boolean inProgress;

    private int siteId; // 考场id
    private int maxOrder; // 该考场最多有多少场面试


    private Interview() {
        isValidated = false;
        sideSelected = false;
        orderSelected = false;
        inProgress = false;
    }

    public static Interview getInstance() {
        if (mInterview == null) {
            mInterview = new Interview();
        }
        return mInterview;
    }

    // status 暂时不管
    public boolean setStatus(InterviewStatus status) {
        // TODO:准备面试和面试进行中
        switch (status) {
            case VALIDATE: // 验证考场
                mStatus = status;
                //TODO: when to set isvalidated = true?
                return true;
            case CHOOSESIDE: // 选择考场
                if (!isValidated)
                    return false;
                mStatus = status;
                //TODO: when to set sideselected = true?
                return true;
            case CHOOSEORDER: // 选择场次
                if (!sideSelected) // TODO: 只能是考官端？？
                    return false;
                mStatus = status;
                //TODO: when to set orderselected = true?
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
                //TODO: when to set inprogress = false?
                return true;
        }
        return false;
    }

    public InterviewStatus getStatus() {
        // TODO
        return mStatus;
    }

    public boolean validateSite(String siteId, String validateCode) {

        // TODO: 调用getResponseFromHttpUrl发送请求给服务器
        // TODO: networkUtils.buildUrl("/?siteId=XXXX&code=XXXX) /validate?siteid=0001&validatecode=0001
        String parameters = "/validate?siteid=" + siteId + "&validatecode=" + validateCode;
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        if (siteId.equals("0000") && validateCode.equals("0000")) {
            isValidated = true;
            mSchoolName = "北京大学";
            mSiteName = "文史楼110";
            return true;
        } else {
            return false;
        }
    }

    public boolean chooseSide(InterviewSide interviewFunction) {
        if (!isValidated) {
            Log.e(TAG, "Code not validated when selecting side!");
            // /side?siteid=0001&side=teacher
            return false;
        }
        if (interviewFunction == InterviewSide.TEACHER) {
            String parameters = "/side?siteid=" + siteId + "&side=teacher";
            URL url = new NetworkUtils().buildUrl(parameters);
            //TODO: new Task().execute(url);
        } else if (interviewFunction == InterviewSide.STUDENT) {
            String parameters = "/side?siteid=" + siteId + "&side=student";
            URL url = new NetworkUtils().buildUrl(parameters);
            //TODO: new Task().execute(url);
        } else {
            return false;
        }
        sideSelected = true;
        mSide = interviewFunction;
        return true;
    }

    public String[] getPeriods() {
        // TODO: getPeriods from server
        return mPeriods;
    }

    // 设置场次
    public boolean setOrder(int index) {
        orderSelected = true;
        orderIndex = index;
        return true;
    }

    // 考官选择场次
    public boolean chooseOrder(int order) {
        // /order?siteid=0001&order=01
        if (order > maxOrder) {
            return false;
        }
        String parameters = "/order?siteid=" + siteId + "&order=" + String.valueOf(order);
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        setOrder(order);
        return true;
    }

    // 考官签到
    public boolean teacherSignIn(String id) {
        // TODO: 验证老师的身份？
        String parameters = "/teacher?siteid=" + siteId + "&order=" + String.valueOf(orderIndex) + "&id=" + id;
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 考官动态查看考生签到情况
    // teacher和student应该在选择考试之后设置好
    public boolean queryStudent() {
        String parameters = "/querystudent?siteid=" + siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 考官点击开始考试
    public boolean start() {
        String parameters = "/start?siteid=" + siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 考官点击结束考试
    public boolean end() {
        String parameters = "/end?siteid=" + siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 学生端查询场次（与考官pad同步）
    public boolean queryOrder() {
        String parameters = "/query?siteid=" + siteId;
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 考生签到
    public boolean studentCheckIn(String id) {
        // TODO: 验证老师的身份？
        String parameters = "/student?siteid=" + siteId + "&order=" + String.valueOf(orderIndex) + "&id=" + id;
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 学生端查询是否可以开始考试
    public boolean queryStart() {
        String parameters = "/querystart?siteid=" + siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }

    // 学生端查询是否可以开始考试
    public boolean queryEnd() {
        String parameters = "/queryend?siteid=" + siteId + "&order=" + String.valueOf(orderIndex);
        URL url = new NetworkUtils().buildUrl(parameters);
        //TODO: new Task().execute(url);
        return true;
    }
}
