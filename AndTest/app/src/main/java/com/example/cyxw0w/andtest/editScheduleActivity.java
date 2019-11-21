package com.example.cyxw0w.andtest;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class editScheduleActivity extends AppCompatActivity
    implements View.OnClickListener,
        OnMapReadyCallback
{
    private DatabaseReference mPostReference;

    EditText edit_id;

    EditText edit_time;
    EditText edit_sch;
    EditText edit_pl;
    EditText edit_memo;
    Button btn_edit;
    Button btn_can;
    Button btn_back;

    String id;
    String time;
    String schedule;
    String place;
    String memo;

    Double lat;
    Double lon;
    private GoogleMap mMap ;

    String strTitle;
    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex = new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        strTitle = getIntent().getStringExtra("title");
        Log.d("getFirebaseDatabase", "key: " + strTitle);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        edit_id = (EditText)  findViewById(R.id.edit_id);
        edit_time = (EditText) findViewById(R.id.edit_time);
        edit_sch = (EditText) findViewById(R.id.edit_sch);
        edit_pl = (EditText) findViewById(R.id.edit_pl);
        edit_memo = (EditText) findViewById(R.id.edit_memo);

        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_can = (Button) findViewById(R.id.btn_can);
        btn_back = (Button) findViewById(R.id.btn_back);

        btn_edit.setOnClickListener(this);
        btn_edit.setEnabled(true);           //최초 수정완료 표시 X
        btn_can.setOnClickListener(this);
        btn_can.setEnabled(true);
        btn_back.setOnClickListener(this);
        btn_back.setEnabled(true);

        //getFirebaseDatabase();
    }

    //if id가 존재할 경우 id값을 받아옴 하지만, 인텐트가 없는 경우 추가 모드
    // (다만 위치정보는 infoWindow로 인텐트 받고, 일정 수정을 통해서 들어온 경우는
    // 인텐트로 id값을 전달받고 해당 내용을 데이터베이스 상에서 입력받음.
    public void setInsertMode() {
        edit_id.setText("");
        edit_time.setText("");
        edit_sch.setText("");
        edit_pl.setText("");
        edit_memo.setText("");
    }

    public void postFirebaseDatabase(boolean add){
        //데이터베이스 참조
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        Map<String, Object> post_poValues = null;
        //데이터를 삭제할건가 말건가(true는 추가, false면 추가안함)
        if(add){
            //FirebasePost클래스의 객체 post선언 (스트링 입력)
            FirebasePost post = new FirebasePost(id, time, schedule, place, memo);

            //post벨류값을 post의 toMap 함수 반환값
            postValues = post.toMap();
            if(lat != null || lon != null){
                FirebasePost post_po = new FirebasePost(id, lat, lon);
                post_poValues = post_po.toMap_pos();
            }
        }
        childUpdates.put("/pos_list/" + id, post_poValues);
        childUpdates.put("/id_list/" + id, postValues);
        mPostReference.updateChildren(childUpdates);
    }


    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey(); //--> 인텐트로 id받아와 그냥, 알겠어? 쉬파 무슨 키달라고 징징거려 알아서 옆에꺼 키 가져다 써야지
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);

                    if(strTitle.equals(String.valueOf(get.id))) {
                        String[] info = {get.id, get.time, get.schedule, get.place, get.memo, String.valueOf(get.Longitude), String.valueOf(get.Latitude)};
                        edit_id.setText(strTitle);
                        edit_time.setText(get.time);
                        edit_sch.setText(get.schedule);
                        edit_pl.setText(get.place);
                        edit_memo.setText(get.memo);
                        if(info[5] != null || info[6] !=  null){
                            lat = get.Latitude;
                            lon = get.Longitude;
                        }
                        Log.d("getFirebaseDatabase", "key: " + key);
                        Log.d("getFirebaseDatabase", "info: " + info[0]);
                        Log.d("getFirebaseDatabase", "info: " + info[1]);
                        Log.d("getFirebaseDatabase", "info: " + info[2]);
                        Log.d("getFirebaseDatabase", "info: " + info[3]);
                        Log.d("getFirebaseDatabase", "info: " + info[4]);
                        Log.d("getFirebaseDatabase", "info: " + info[5]);
                        Log.d("getFirebaseDatabase", "info: " + info[6]);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild("id");
        sortbyAge.addListenerForSingleValueEvent(postListener);
        Query sortbyPos = FirebaseDatabase.getInstance().getReference().child("pos_list").orderByChild("id");
        sortbyPos.addListenerForSingleValueEvent(postListener);
    }

    @Override
    public void onClick(View v) {
        //버튼 별로 지정 (이거 카테고리 쓸때 설정하면 좋겠다!)
        switch (v.getId()) {
            case R.id.btn_edit:
            id = edit_id.getText().toString();
                time = edit_time.getText().toString();
                schedule = edit_sch.getText().toString();
                place = edit_pl.getText().toString();
                memo = edit_memo.getText().toString();

                postFirebaseDatabase(true);
                getFirebaseDatabase();
                setInsertMode();
                Toast.makeText(this, "수정 완료", Toast.LENGTH_LONG).show();
                //무슨 역할일까, 대충 editText값 초기화하는거같은데..
                //edit_time.requestFocus();
                //edit_time.setCursorVisible(true);

                Intent intent = new Intent(editScheduleActivity.this, MainActivity.class);
                //intent.putExtra("id", id);        //time 정보 전달
                startActivity(intent);
                break;

                //예약취소
            case R.id.btn_can:
                AlertDialog.Builder dialog = new AlertDialog.Builder(editScheduleActivity.this);
                dialog.setTitle("데이터 삭제")
                        .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + edit_time)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postFirebaseDatabase(false);    //데이터 요청 보냄
                                getFirebaseDatabase();  //데이터 확인
                                setInsertMode();    //입력모드 실행(최초상태)
                                edit_time.setEnabled(true);   //아이디 입력 가능
                                Toast.makeText(editScheduleActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(editScheduleActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                                setInsertMode();
                                edit_time.setEnabled(true);
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.btn_back:
               finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Intent intent = getIntent();
        double latParam = intent.getDoubleExtra("latParam", 37.56);     //lat값 받음
        double lonParam = intent.getDoubleExtra("lonParam",126.97);     //lon값 받음
        Log.d("mvCamera", "moveCamera: moving the camera to: lat: " + latParam + ", lng: " + lonParam );

        lat = latParam;
        lon = lonParam;

        mMap = map;

        LatLng meetPlace = new LatLng(lat, lon);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(meetPlace);

        map.addMarker(markerOptions);
        moveCamera(meetPlace, 17.0f);

//        Toast.makeText(this, "중간 지점 생성완료", Toast.LENGTH_SHORT).show();
    }
    private void moveCamera(LatLng latLng, float zoom){
        Log.d("mvCamera", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
