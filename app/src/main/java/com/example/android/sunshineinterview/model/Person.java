package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.google.gson.JsonObject;

public class Person{
    private static final String TAG = "Person";
    public String id;
    public String name;
    public String imgUrl;
    public boolean isAbsent;
    public String storageImgUrl;
    Person(JsonObject j){
        id = j.get("id").getAsString();
        name = j.get("name").getAsString();
        Log.v(TAG, "id = " + id);
        Log.v(TAG, "name = " + name);

        if(j.has("is_absent")){
            isAbsent = Boolean.valueOf(j.get("is_absent").getAsString());
            Log.v(TAG, "is_absent = " + isAbsent);
        }
        if(j.has("img_url")){
            imgUrl = j.get("img_url").getAsString();
            Log.v(TAG, "imgUrl = " + imgUrl);
        }

        storageImgUrl = "";
    }
    Person(String id, String name){
        this.id = id;
        this.name = name;
        this.isAbsent = true;
        this.imgUrl = "";

        storageImgUrl = "";
    }

    public void setStorageImgUrl(String url) {
        storageImgUrl = url;
    }
    public void setImgUrl(String url){
        this.imgUrl = url;
    }
    public void setIsAbsent(boolean status){
        isAbsent = status;
    }
}
