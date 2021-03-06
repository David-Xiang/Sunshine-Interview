package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.commonactivities.UploadMainActivity;
import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.studentactivities.StudentEndActivity;
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
    private boolean signinSkipped;
    private int orderIndex;
    private static int interviewID;

    private ArrayList<Person> mTeachers;
    private ArrayList<Person> mStudents;

    public ArrayList<String> pathList;
    public ArrayList<String> nameList;

    public void addNameAndPath(String name, String path) {
        pathList.add(path);
        nameList.add(name);
    }

    public String getPathFromName(String name) {
        for (int i = 0; i < nameList.size(); i++) {
            if (nameList.get(i).equals(name))
                return pathList.get(i);
        }
        return "";
    }

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

        pathList = new ArrayList<>();
        nameList = new ArrayList<>();
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

    public boolean setSigninSkipped(boolean skipped)
    {
        signinSkipped = skipped;
        return true;
    }

    public boolean isSigninSkipped()
    {
        return signinSkipped;
    }

    // API for frontend
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
    public static String getInterviewID() {
        return interviewID + "";
    }

    public boolean validId(String id){
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

    // TODO: new validate function for uploading videos
    public boolean validate(UploadMainActivity validateActivity, String siteId, String validateCode) {
        if (!validId(siteId) || !validId(validateCode))
            return false;
        String parameters = "/validate?siteid=" + siteId + "&validatecode=" + validateCode;
        Log.v(TAG, "validate() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new ValidateVideoTask().execute(validateActivity, url);
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
            parameters = "/side?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&side=teacher";
            mSide = InterviewSide.TEACHER;
        } else if (interviewFunction == InterviewSide.STUDENT) {
            parameters = "/side?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&side=student";
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
    public boolean setInterviewID(int id){
        Log.v(TAG, "setid to " + id);
        interviewID = id;
        return true;
    }

    public boolean setOrder(String order){
        orderSelected = true;
        Log.v(TAG, "setOrder to " + order);
        int i = 0;
        for (Period p: mInterviewInfo.periods){
            Log.v(TAG, "setOrder(): check " + p.order);
            if (order.equals(p.order)){
                orderIndex = i;
                return true;
            }
            i++;
        }
        return false;
    }


    // 考官选择场次
    public boolean chooseOrder(ChooseOrderActivity chooseOrderActivity, int order) {
        if (!sideSelected) {
            Log.v(TAG, "chooseOrder(): side not selected yet!");
            return false;
        } else if (order >= mInterviewInfo.periods.size()) {
            Log.v(TAG, "chooseOrder(): order out of bound");
            return false;
        } else if (mSide != InterviewSide.TEACHER) {
            Log.v(TAG, "choose Order(): not on teacher side");
            return false;
        }
        String parameters;
        orderIndex = order;
        parameters = "/order";
        if (isSigninSkipped())
            parameters = "/skip";
        parameters += "?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
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
        for (int i = 0; i < p.teachers.size(); i++) {
            String id  = p.teachers.get(i).id;
            String name = p.teachers.get(i).name;
            mTeachers.add(new Person(id, name));
        }
        for (int i = 0; i < p.students.size(); i++) {
            String id = p.students.get(i).id;
            String name = p.students.get(i).name;
            mStudents.add(new Person(id, name));
        }
        return true;
    }
    public ArrayList<String> getTeacherNames(){
        int size = mTeachers.size();
        ArrayList<String> teacherNames = new ArrayList<>();
        for (int i = 0; i < size; i++){
            teacherNames.add(mTeachers.get(i).name);
        }
        return teacherNames;
    }
    public ArrayList<String> getStudentNames(){
        int size = mStudents.size();
        ArrayList<String> studentNames = new ArrayList<>();
        for (int i = 0; i < size; i++){
            studentNames.add(mStudents.get(i).name);
        }
        return studentNames;
    }

    public ArrayList<Person> getStudents(){
        return mStudents;
    }

    // 返回学生是否缺席
    public boolean getAbsent(String Id) {
        for (Person student : mStudents) {
            if (student.id.equals(Id))
                return student.isAbsent;
        }
        return true;
    }
    public void signin(String id, String name){
        for (int i = 0; i < mStudents.size(); i++) {
            if (mStudents.get(i).name.equals(name)) {
                mStudents.get(i).id = id;
                mStudents.get(i).isAbsent = false;
                break;
            }
        }
    }

    public void setStudentPhotoPath(int index, String name, String url) {
        for (int i = 0; i < mStudents.size(); i++) {
            if (mStudents.get(i).name.equals(name)) {
                mStudents.get(i).storageImgUrl = url;
                break;
            }
        }
        for (int i = 0; i < mStudents.size(); i++)
        {
            Log.d(TAG, mStudents.get(i).name + " " + mStudents.get(i).storageImgUrl);
        }
        Log.d(TAG, index + " " + url);
    }

    public void setStudentPhotoPath(String Id, String url) {
        for (int i = 0 ; i < mStudents.size(); i++) {
            if (mStudents.get(i).id.equals(Id)) {
                mStudents.get(i).setStorageImgUrl(url);
                break;
            }
        }
    }

    public String getimageUrl(String Id) {
        for (Person student : mStudents) {
            if (student.id.equals(Id))
                return student.imgUrl;
        }
        return null;
    }

    public String getInterviewTime(){
        Period p = mInterviewInfo.periods.get(orderIndex);
        return p.startTime + '-' + p.endTime;
    }

    public String getStatusString(){
        String[] StatusText = {"验证考场", "选择用户端", "选择考次", "签到", "就绪", "面试进行中", "面试已结束"};
        return StatusText[mStatus.ordinal()];
    }


    // 考官签到
    // teacherSignIn函数增加img参数，表示的是和该教师相对应的照片文件名，例如"1234.jpg"
    // teacher?siteid=0000&order=00&img=1234.jpg
    // 上传文件到服务器指定目录下：new UploadTask().execute(img_path);
    // studentSignIn函数同理
    // 数据库端需要新增“人”与“图片”对应的数据结构存储这种对应关系
    // 服务器端需要指定图片存储的位置
    public boolean teacherSignin(TeacherSigninActivity teacherSigninActivity,
                                 int teacherIndex, String path) {
        if (!orderSelected) {
            Log.v(TAG, "teacherSignIn(): order not selected");
            return false;
        } else if (mSide != InterviewSide.TEACHER) {
            Log.v(TAG, "teacherSignIn(): must be on teacher's side");
            return false;
        }
        String id = mInterviewInfo.periods.get(orderIndex).teachers.get(teacherIndex).id;
        String filename = new String();
        filename = path.substring(path.lastIndexOf('/') + 1);
        String parameters = "/teacher?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order="
                + getOrderString() + "&id=" + id + "&img=" + filename;
        Log.v(TAG, "teacherSignin() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new UploadTask().execute("0", path, id, mInterviewInfo.collegeId);
        new TeacherSigninTask().execute(teacherSigninActivity, url);
        return true;
    }

    // 考官动态查看考生签到情况
    public boolean queryStudent(WaitForStudentSigninActivity waitForStudentSigninActivity) {
        if (!orderSelected) {
            Log.v(TAG, "queryStudent(): order not selected");
            return false;
        } else if (mSide != InterviewSide.TEACHER) {
            Log.v(TAG, "queryStudent(): must be on teacher's side");
            return false;
        }
        String parameters = "/querystudent?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryStudentTask().execute(waitForStudentSigninActivity, url);
        return true;
    }

    // 考官点击开始考试
    public boolean start(WaitForStudentSigninActivity waitForStudentSigninActivity) {
        if (!orderSelected) {
            Log.w(TAG,  "start(): order not selected");
            return false;
        } else if (mSide != InterviewSide.TEACHER) {
            Log.w(TAG,  "start(): teacher side");
            return false;
        }
        String parameters = "/start?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
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
        String parameters = "/end?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
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
        String parameters = "/queryorder?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId;
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryTask().execute(waitForChooseOrderActivity, url);
        return true;
    }


    // 考生签到
    public boolean studentSignin(StudentSigninActivity studentSigninActivity,
                                 int studentIndex, String path) {
        if (!orderSelected) {
            Log.w(TAG,  "queryStart(): order not selected");
            return false;
        } else if (mSide != InterviewSide.STUDENT) {
            Log.w(TAG,  "queryStart(): not on student side");
            return false;
        }
        String filename = new String();
        filename = path.substring(path.lastIndexOf('/') + 1);
        String id = mInterviewInfo.periods.get(orderIndex).students.get(studentIndex).id;
        String parameters = "/student?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order="
                + getOrderString() + "&id=" + id + "&img=" + filename;
        Log.v(TAG, "studentSignin() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        new UploadTask().execute("0", path, id, mInterviewInfo.collegeId);
        new StudentSigninTask().execute(studentSigninActivity, url);
        return true;
    }

    // 学生端查询是否可以开始考试
    public boolean queryStart(StudentSigninActivity studentSigninActivity) {
        if (!orderSelected) {
            Log.w(TAG,  "queryStart(): order not selected");
            return false;
        } else if (mSide != InterviewSide.STUDENT) {
            Log.w(TAG,  "queryStart(): not on student side");
            return false;
        }
        String parameters = "/querystart?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryStartInSigninTask().execute(studentSigninActivity, url);
        return true;
    }

    public boolean queryStart(WaitForTeacherConfirmActivity waitForTeacherConfirmActivity) {
        if (!orderSelected) {
            Log.w(TAG,  "queryStart(): order not selected");
            return false;
        } else if (mSide != InterviewSide.STUDENT) {
            Log.w(TAG,  "queryStart(): not on student side");
            return false;
        }
        String parameters = "/querystart?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryStartTask().execute(waitForTeacherConfirmActivity, url);
        return true;
    }

    // 学生端查询是否已经结束考试考试
    public boolean queryEnd(StudentInProgressActivity studentInProgressActivity) {
        if (getStatus() != InterviewStatus.INPROGRESS) {
            Log.w(TAG, "queryEnd(): interview not in progress");
            return false;
        } else if (mSide != InterviewSide.STUDENT){
            Log.w(TAG,  "queryEnd(): not on student side");
            return false;
        }
        String parameters = "/queryend?collegeid=" + mInterviewInfo.collegeId + "&siteid=" + mInterviewInfo.siteId + "&order=" + getOrderString();
        URL url = NetworkUtils.buildUrl(parameters);
        new QueryEndTask().execute(studentInProgressActivity, url);
        return true;
    }

    private String getOrderString(){
        return mInterviewInfo.periods.get(orderIndex).order;
    }

    public void clearInfo() {
        pathList = null;
        nameList = null;
    }
}
