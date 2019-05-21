package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.utilities.NetworkUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UploadTask extends AsyncTask<String, Boolean, Boolean> {
    private static final String TAG = "uploadTask";

    @Override
    protected Boolean doInBackground(String... params) {
        // 新增一个参数，第一个参数表示是上传图片("0")还是上传视频("1")
        // 如果是上传视频，在根目录列出所有文件的列表，找出其中的.mp4文件
        // 然后逐个上传，实时打log：正在上传什么文件，
        Log.d(TAG, "in UploadTask()  " + params[0]);
        if (params[0].equals("0")) {
            // 上传文件
            String string = params[1]; // 本地完整路径
            String id = params[2];
            File file = new File(string);
            if (!file.exists()) {
                Log.v(TAG, "UploadTask() file does not exist?: " + string);
                return false;
            }
            // check exist
            String filename = string.substring(string.lastIndexOf('/') + 1);
            String parameters = "/upload/images/" + filename + "?id=" + id + "&collegeid=" + params[3];
            Log.v(TAG, "UploadTask() sending url = " + parameters);
            URL url = NetworkUtils.buildUrl(parameters);
            Log.v(TAG, "Begin uploading!");
            return NetworkUtils.uploadFile(file, id, url);
        } else {
            // 上传视频
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SunshineInterview");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e("upload video", "SunshineInterview failed to create directory");
                    return false;
                }
            }
            ArrayList<ArrayList<String>> videoList = new ArrayList<> ();
            ArrayList<String> interviewList = new ArrayList<> (); // 保存完整的路径！
            Log.d(TAG, "in UploadTask(): ready to loop");
            for (File temp : mediaStorageDir.listFiles()) {
                if (temp.isDirectory()) {
                    continue;
                }
                String Filepath = temp.toString();
                //Log.d(TAG, "in ..UploadTask(): in loop: " + Filepath.substring(Filepath.lastIndexOf('.') + 1));
                if (!Filepath.substring(Filepath.lastIndexOf('.') + 1).equals("mp4"))
                    continue;
                String filename = Filepath.substring(Filepath.lastIndexOf('/') + 1);
                Log.d(TAG, "in UploadTask(): in loop: " + filename);
                String interviewID = filename.substring(
                        filename.indexOf('_') + 1, filename.lastIndexOf('_'));
                Log.d(TAG, "in UploadTask(): in loop, interviewID: " + interviewID);
                // 如果有，就按index在videoList中找到相应数组，添加
                // 如果没有，append interviewList, new videoList
                if (interviewList.contains(interviewID)) {
                    int index = interviewList.indexOf(interviewID);
                    videoList.get(index).add(Filepath);
                    Log.d("add path to list: ", "" + videoList.get(index).size());
                } else {
                    interviewList.add(interviewID);
                    ArrayList<String> temparray = new ArrayList<> ();
                    temparray.add(Filepath);
                    videoList.add(temparray);
                }

            }
            // 按时间顺序排序
            Log.d(TAG, "in UploadTask(): ready to sort");
            for (ArrayList<String> interviewVideo : videoList) {
                // Collections.sort(interviewVideo);
                Collections.sort(interviewVideo, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareTo(s2);
                    }
                });
            }

            int index = 0;
            int videoindex;
            for (ArrayList<String> interviewVideo : videoList) {
                videoindex = 0;
                for (String video : interviewVideo) {
                    // android上传视频 /upload/videos/(interviewid)/0.mp4（从0开始编号）
                    // interviewID从文件名中获取
                    String filename = video.substring(video.lastIndexOf('/') + 1);
                    String interviewID = filename.substring(
                            filename.indexOf('_') + 1, filename.lastIndexOf('_'));
                    //Log.v(TAG, "UploadTask() temp" + temp);
                    //String interviewID = temp.substring(temp.indexOf('_') + 1, temp.lastIndexOf('_'));
                    String parameters = "/upload/videos/"  + interviewID + "/" + videoindex
                            + ".mp4?interviewid=" + interviewID;
                    Log.v(TAG, "UploadTask() sending url = " + parameters);
                    URL url = NetworkUtils.buildUrl(parameters);
                    File file = new File(video);

                    if (!NetworkUtils.uploadFile(file, videoindex + ".mp4", url))
                        Log.e(TAG, "error in UploadTask() sending url = " + parameters);
                    videoindex += 1;
                }
                index += 1;
            }
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(!bool) {
            Log.w(TAG, "Something is wrong when uploading picture/videos");
        }
    }
}
