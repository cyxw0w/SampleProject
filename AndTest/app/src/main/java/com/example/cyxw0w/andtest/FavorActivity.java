package com.example.cyxw0w.andtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


// 리스트뷰를 추가한다.
//데이터베이스에서 저장된 장소 정보를 가져온다.
//스크롤 뷰 속에 리스트 뷰로
//클릭시 해당 장소에 대한 정보가 뜬다.
// 해당 정보에서 에딧 스케쥴로 인텐트하는 yes,no 대화상자가 뜬다.
//끝이다
public class FavorActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);

    }

    public void backtoMenu(View view)
    {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
