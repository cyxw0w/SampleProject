package com.example.cyxw0w.andtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity
{
    private DatabaseReference mPostReference;

    String test;

    Button button;
    String schedule;
    String place;
    String memo;


    String id;
    String time;
    String sort = "time";
    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayInfo =  new ArrayList<String>();
    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_layout);

        //simple_list_item_1의 형태를 사용함
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //리스트 뷰 적용
        //짧게 클릭, 길게 클릭
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        //리스트 형태 적용
        listView.setAdapter(arrayAdapter);
        //짧게 클릭한 경우, 수정모드
        listView.setOnItemClickListener(onClickListener);
        //길게 클릭한 경우, 삭제(대화상자) 모드
        listView.setOnItemLongClickListener(longClickListener);

        getFirebaseDatabase();
    }


    // 스케줄 뷰로 이동하는 함수(리스너)
    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        //View 버튼 클릭시, tempData에 있는 스트링이 각기 editText에 들어가며,
        // tempData의 3번째 주소에 있는 젠더가 체크박스표시,
        // 나머지 edit_ID는 더이상 수정 불가, insert키도 수정불가
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("On Click", "position = " + position);
            Log.e("On Click", "Data: " + arrayInfo.get(position));
            String tempData = arrayInfo.get(position);  //스왑용(배열 데이터, split에서 받음?
            Log.e("On Click", "Split Result = " + tempData);

            // 확인 로그
            Toast.makeText(getApplicationContext(), "일정 보기", Toast.LENGTH_SHORT).show();

            // 액티비티 넘어가기
            Intent intent;
            intent = new Intent(getApplicationContext(), ScheduleActivity.class);
            intent.putExtra("title", tempData);        //id정보 전달
            startActivity(intent);
        }
    };

    //삭제
    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        //스크롤뷰안에 있는 리스트 뷰를 꾹~ 클릭했을 경우,
        //
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Long Click", "position = " + position);
            final String[] nowData = arrayData.get(position).split("\\s+");
            //첫 주소값 전달(ID 기준으로 삭제)
            time = nowData[0];
            //뷰데이터 - 대화상자 내부에 넣기 위함
            // String viewData = nowData[0] + ", " + nowData[1] + ", " + nowData[2] + ", " + nowData[3];
            //대화상자 열기
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postFirebaseDatabase(false);    //데이터 요청 보냄
                            getFirebaseDatabase();  //데이터 확인
                            Toast.makeText(MainActivity.this, "일정을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    };

    public void postFirebaseDatabase(boolean add){
        //데이터베이스 입력인데, 해당 엑티비티에서는 입력하지 않음.
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            //FirebasePost클래스의 객체 post선언 (스트링 입력)
            FirebasePost post = new FirebasePost(id, time, schedule, place, memo);
            //post벨류값을 post의 toMap 함수 반환값
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + time, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayData.clear();
                arrayIndex.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);

                    String[] info = {get.id, get.time, get.schedule, get.place, get.memo};
                    String Result = setTextLength(info[0],10) + setTextLength(info[1],10) + setTextLength(info[2],10) + setTextLength(info[3],10) + setTextLength(info[4],10);
                    String Idnum = info[0];
                    //주소 저장
                    //아이디 저장
                    arrayInfo.add(Idnum);
                    arrayData.add(Result);
                    arrayIndex.add(key);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0]);
                    Log.d("getFirebaseDatabase", "info: " + info[1]);
                    Log.d("getFirebaseDatabase", "info: " + info[2]);
                    Log.d("getFirebaseDatabase", "info: " + info[3]);
                    Log.d("getFirebaseDatabase", "info: " + info[4]);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(arrayData);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        //정렬
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild(sort);
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }

    public void onGpsView(View view)
    {
        Intent intent = new Intent(MainActivity.this, GoogleTest.class);
        startActivity(intent);
    }

    // 일정 추가 함수
    public void addSchedule(View view)
    {

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
