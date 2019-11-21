package com.example.cyxw0w.andtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class searchAddActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
    }

    //지도 버튼 클릭
    public void mapClick(View view)
    {
    Intent intent = new Intent(this, GoogleTest.class);
    startActivity(intent);
    }

}
