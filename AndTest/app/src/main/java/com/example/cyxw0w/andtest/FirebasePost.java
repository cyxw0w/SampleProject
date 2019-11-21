package com.example.cyxw0w.andtest;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DowonYoon on 2017-07-11.
 */

@IgnoreExtraProperties
public class FirebasePost {
    public String id;
    public String name;
    public Long age;
    public String gender;


    public String time;
    public String schedule;
    public String place;
    public String memo;


    //좌표 정보 저장
    public String Position;
    public Double Latitude;
    public Double Longitude;

    public FirebasePost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }
    //일정 정보 데이터베이스 입력
    public FirebasePost(String id, String time, String schedule, String place, String memo) {
        this.id = id;
        this.time =  time;
        this.schedule = schedule;
        this.place = place;
        this.memo = memo;
    }
    //좌표 정보 데이터베이스 입력
    public FirebasePost(String Position, Double Lat, Double Lon){
        this.Position = Position;
        this.Latitude = Lat;
        this.Longitude = Lon;
    }

    //Map<Str, Obj> 형태 반환하는 함수
    @Exclude
    public Map<String, Object> toMap() {
        //
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("time", time);
        result.put("schedule", schedule);
        result.put("place", place);
        result.put("memo", memo);
        return result;
    }

    @Exclude
    public Map<String, Object> toMap_pos() {
        //
        HashMap<String, Object> result = new HashMap<>();
        result.put("position", Position);
        result.put("latitude", Latitude);
        result.put("longitute", Longitude);
        return result;
    }
}