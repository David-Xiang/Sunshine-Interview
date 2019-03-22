package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Period{
    private static final String TAG = "Period";
    public String startTime, endTime;
    public ArrayList<Person> teachers, students;
    public String order;
    Period(JsonObject j){
        order = j.get("order").getAsString();
        startTime = j.get("start_time").getAsString();
        endTime = j.get("end_time").getAsString();
        teachers = new ArrayList<>();
        students = new ArrayList<>();
        students.add(new Person("12", "xdw"));

        Log.v(TAG, "order = " + order);
        Log.v(TAG, "startTime = " + startTime);
        Log.v(TAG, "endTime = " + endTime);

        Log.v(TAG, "students: ");
        JsonArray studentsArray = j.getAsJsonArray("student");
        for (JsonElement e: studentsArray){
            students.add(new Person(e.getAsJsonObject()));
        }

        Log.v(TAG, "teachers: ");
        JsonArray teachersArray = j.getAsJsonArray("teacher");
        for (JsonElement e: teachersArray){
            teachers.add(new Person(e.getAsJsonObject()));
        }
    }
}
