package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.studentactivities.StudentInProgressActivity;
import com.example.android.sunshineinterview.studentactivities.StudentSigninActivity;
import com.example.android.sunshineinterview.studentactivities.WaitForChooseOrderActivity;
import com.example.android.sunshineinterview.studentactivities.WaitForTeacherConfirmActivity;
import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.android.sunshineinterview.teacheractivities.TeacherEndActivity;
import com.example.android.sunshineinterview.teacheractivities.TeacherInProgressActivity;
import com.example.android.sunshineinterview.teacheractivities.TeacherSigninActivity;
import com.example.android.sunshineinterview.teacheractivities.WaitForStudentSigninActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.example.android.sunshineinterview.task.*;

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

    private ArrayList<Person> mTeachers;
    private ArrayList<Person> mStudents;

    private Interview() {
        isValidated = false;
        sideSelected = false;
        orderSelected = false;

        orderIndex = -1;

        mStatus = InterviewStatus.END;
        mSide = InterviewSide.UNKNOWN;
        mInterview = null;
        mInterviewInfo = null;
        mTeachers = null;
        mStudents = null;
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

    public InterviewSide getSide(){
        return mSide;
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
            case INPROGRESS:
                mStatus = status;
                return true;
            case END: // 结束面试
                mStatus = status;
                return true;
        }
        return false;
    }

    public boolean validId(String id) {
        return id.length() == 4;
    }

    public boolean validate(ValidateActivity validateActivity, String siteId, String validateCode) {
        if (!validId(siteId) || !validId(validateCode))
            return false;
        String parameters = "/validate?siteid=" + siteId + "&validatecode=" + validateCode;
        Log.v(TAG, "validate() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new ValidateTask().execute(validateActivity, url);
        return true;
    }

    public boolean setValidated() {
        isValidated = true;
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
        Log.v(TAG, "chooseSide() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new ChooseSideTask().execute(chooseSideActivity, url);
        return true;
    }

    // 设置场次，可能是考官端收到场次确认，或者考生端收到场次确认
    public boolean setOrder(int index) {
        orderSelected = true;
        orderIndex = index;
        return true;
    }

    // 考官选择场次
    public boolean chooseOrder(ChooseOrderActivity chooseOrderActivity, int order) {
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
        Log.v(TAG, "chooseOrder() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new ChooseOrderTask().execute(chooseOrderActivity, url);
        return true;
    }

    public boolean updatePersonInfo(){
        // update mPersonInfo cache from Interview
        // foreground can directly get teachers&students' names
        if (!orderSelected){
            Log.v(TAG, "updatePersonInfo(): haven't chosen order yet!");
            return false;
        }
        mTeachers = new ArrayList<>();
        mStudents = new ArrayList<>();
        Period p = mInterviewInfo.periods.get(orderIndex);
        for (int i = 0; i < p.teachers.size(); i++){
            String id  = p.teachers.get(i).id;
            String name = p.teachers.get(i).name;
            mTeachers.add(new Person(id, name));
        }
        for (int i = 0; i < p.students.size(); i++){
            String id = p.students.get(i).id;
            String name = p.students.get(i).name;
            mStudents.add(new Person(id, name));
        }
        return true;
    }
    public String [] getTeacherNames(){
        int size = mTeachers.size();
        String [] teacherNames = new String[size];
        for (int i = 0; i < size; i++){
            teacherNames[i] = mTeachers.get(i).name;
        }
        return teacherNames;
    }
    public String [] getStudentNames(){
        int size = mStudents.size();
        String [] studentNames = new String[size];
        for (int i = 0; i < size; i++){
            studentNames[i] = mStudents.get(i).name;
        }
        return studentNames;
    }

    public String getInterviewTime(){
        Period p = mInterviewInfo.periods.get(orderIndex);
        return p.startTime + '-' + p.endTime;
    }

    public String getStatusString(){
        String[] StatusText = {"验证考场", "选择考次", "选择用户端", "签到", "就绪", "面试进行中", "面试已结束"};
        return StatusText[mStatus.ordinal()];
    }


    // 考官签到
    public boolean teacherSignin(TeacherSigninActivity teacherSigninActivity, int teacherIndex) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.TEACHER)
            return false;
        String id = mInterviewInfo.periods.get(orderIndex).teachers.get(teacherIndex).id;
        String parameters = "/teacher?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex) + "&id=" + id;
        Log.v(TAG, "teacherSignin() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new TeacherSigninTask().execute(teacherSigninActivity, url);
        return true;
    }

    // 考官动态查看考生签到情况
    public boolean queryStudent(WaitForStudentSigninActivity waitForStudentSigninActivity) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.TEACHER)
            return false;
        String parameters = "/querystudent?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryStudentTask().execute(waitForStudentSigninActivity, url);
        return true;
    }

    // 考官点击开始考试
    public boolean start(WaitForStudentSigninActivity waitForStudentSigninActivity) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.TEACHER)
            return false;
        String parameters = "/start?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = NetworkUtils.buildUrl(parameters);
        new StartTask().execute(waitForStudentSigninActivity, url);
        return true;
    }

    // 考官点击结束考试
    public boolean end(TeacherInProgressActivity teacherInProgressActivity) {
        if (mSide != InterviewSide.TEACHER && getStatus() != InterviewStatus.INPROGRESS){
            Log.e(TAG, "end(): something is wrong.");
            return false;
        }
        String parameters = "/end?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = NetworkUtils.buildUrl(parameters);
        new EndTask().execute(teacherInProgressActivity, url);
        return true;
    }

    // 学生端查询场次（与考官pad同步）
    public boolean query(WaitForChooseOrderActivity waitForChooseOrderActivity) {
        if (!sideSelected) {
            Log.w(TAG, "query(): in fault 1");
            return false;
        } else if (mSide != InterviewSide.STUDENT){
            Log.w(TAG, "query(): in fault 2");
            return false;
        }
        String parameters = "/query?siteid=" + mInterviewInfo.siteId;
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryTask().execute(waitForChooseOrderActivity, url);
        return true;
    }

    // 考生签到
    public boolean studentSignin(StudentSigninActivity studentSigninActivity, int studentIndex) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String id = mInterviewInfo.periods.get(orderIndex).students.get(studentIndex).id;
        String parameters = "/student?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex) + "&id=" + id;
        URL url = NetworkUtils.buildUrl(parameters);
        new StudentSigninTask().execute(studentSigninActivity, url);
        return true;
    }

    // 学生端查询是否可以开始考试
    public boolean queryStart(StudentSigninActivity studentSigninActivity) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String parameters = "/querystart?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryStartInSigninTask().execute(studentSigninActivity, url);
        return true;
    }

    public boolean queryStart(WaitForTeacherConfirmActivity waitForTeacherConfirmActivity) {
        if (!orderSelected) {
            return false;
        } else if (mSide != InterviewSide.STUDENT)
            return false;
        String parameters = "/querystart?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryStartTask().execute(waitForTeacherConfirmActivity, url);
        return true;
    }

    // 学生端查询是否已经结束考试考试
    public boolean queryEnd(StudentInProgressActivity studentInProgressActivity) {
        if (getStatus() != InterviewStatus.INPROGRESS) {
            Log.w(TAG, "queryEnd(): fault 1");
            return false;
        } else if (mSide != InterviewSide.STUDENT){
            Log.w(TAG,  "queryEnd(): fault 2");
            return false;
        }
        String parameters = "/queryend?siteid=" + mInterviewInfo.siteId + "&order=" + String.valueOf(orderIndex);
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryEndTask().execute(studentInProgressActivity, url);
        return true;
    }
}
