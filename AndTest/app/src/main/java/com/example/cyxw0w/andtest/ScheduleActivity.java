package com.example.cyxw0w.andtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity  extends AppCompatActivity
    implements View.OnClickListener
{
    private DatabaseReference mPostReference;

    Button btn_edit;
    Button btn_can;
    Button btn_back;

    String sort = "id";

    String strTime;
    String strSchedule;
    String strPlace;
    String strMemo;
    String strTitle;


    TextView tvTime;
    TextView tvSchedule;
    TextView tvPlace;
    TextView tvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_layout);

        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        btn_can = (Button) findViewById(R.id.btn_can);
        btn_can.setOnClickListener(this);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        TextView tvTitle = (TextView) findViewById(R.id.text_title);
        tvTime = (TextView) findViewById(R.id.edit_time);
        tvSchedule = (TextView) findViewById(R.id.edit_sch);
        tvPlace = (TextView) findViewById(R.id.edit_pl);
        tvMemo = (TextView) findViewById(R.id.edit_memo);

        strTitle = getIntent().getStringExtra("title");
        Log.d("getFirebaseDatabase", "key: " + strTitle);

        tvTitle.setText(strTitle);


        getFirebaseDatabase();
    }

    public void postFirebaseDatabase(boolean add){
        String time = getIntent().getStringExtra("time");
        //데이터베이스 참조
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        //데이터를 삭제할건가 말건가(true는 추가, false면 추가안함)
        /*
        if(add){
            //FirebasePost클래스의 객체 post선언 (스트링 입력)
            FirebasePost post = new FirebasePost(time, schedule, place, memo);
            //post벨류값을 post의 toMap 함수 반환값
            postValues = post.toMap();
        }
        */
        childUpdates.put("/id_list/" + time, postValues);
        mPostReference.updateChildren(childUpdates);
    }


    public void getFirebaseDatabase() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    if(strTitle.equals(String.valueOf(get.id))) {
                        strTime = get.time;
                        strSchedule = get.schedule;
                        strPlace = get.place;
                        strMemo = get.memo;
                        Log.d("getFirebaseDatabase", "key: " + key);
                        Log.d("getFirebaseDatabase", "info: " + get.id);
                        Log.d("getFirebaseDatabase", "time: " + strTime);
                        Log.d("getFirebaseDatabase", "Sche: " + strSchedule);
                        Log.d("getFirebaseDatabase", "place: " + strPlace);
                        Log.d("getFirebaseDatabase", "memo: " + strMemo);

                        tvTime.setText(strTime);
                        tvSchedule.setText(strSchedule);
                        tvPlace.setText(strPlace);
                        tvMemo.setText(strMemo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild(sort);
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public void onClick(View view){
        switch (view.getId()) {
            //수정 버튼
            case R.id.btn_edit:
                Intent intent = new Intent(this, editScheduleActivity.class);
                intent.putExtra("title", strTitle);
                startActivity(intent);
                break;
                //확인 버튼
            case R.id.btn_back:
                finish();
                break;
                //일정 취소 버튼
            case R.id.btn_can:
                AlertDialog.Builder dialog = new AlertDialog.Builder(ScheduleActivity.this);
                dialog.setTitle("데이터 삭제")
                        .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postFirebaseDatabase(false);    //데이터 요청 보냄
                               // getFirebaseDatabase();  //데이터 확인
                                Toast.makeText(ScheduleActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ScheduleActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }
}