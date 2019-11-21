package com.example.cyxw0w.andtest;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;

public class positionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_layout);

        Intent intent = getIntent();
        double latParam = intent.getDoubleExtra("latParam", 37.56);     //lat값 받음
        double lonParam = intent.getDoubleExtra("lonParam",126.97);     //lon값 받음

    }
}
