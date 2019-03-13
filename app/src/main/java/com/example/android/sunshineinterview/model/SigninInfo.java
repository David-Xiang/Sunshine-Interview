package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class SigninInfo{
    public static final String TAG = "SigninInfo";
    public ArrayList<Person> students;
    SigninInfo(JsonArray j){
        Log.v(TAG,"students: ");
        students = new ArrayList<>();
        for (JsonElement e: j){
            students.add(new Person(e.getAsJsonObject()));
        }
    }
}