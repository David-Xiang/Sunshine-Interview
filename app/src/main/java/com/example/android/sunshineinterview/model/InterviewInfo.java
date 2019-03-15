package com.example.android.sunshineinterview.model;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class InterviewInfo{
    public static final String TAG = "InterviewInfo";
    public String collegeId;
    public String collegeName;
    public String siteId;
    public String siteName;
    public ArrayList<Period> periods;

    InterviewInfo(JsonObject j){
        collegeId = j.get("college_id").getAsString();
        collegeName = j.get("college_name").getAsString();
        siteId = j.get("site_id").getAsString();
        siteName = j.get("site_name").getAsString();

        periods = new ArrayList<>();
        Log.v(TAG,"Periods");
        JsonArray periodsArray = j.get("periods").getAsJsonArray();

        for (JsonElement e: periodsArray){
            periods.add(new Period(e.getAsJsonObject()));
        }
    }
}