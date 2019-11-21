package com.example.cyxw0w.andtest;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MenuActivity extends AppCompatActivity
    {
        private static final String TAG = "MainActivity";

        private static final int ERROR_DIALOG_REQUEST = 9001;

        private void init(){
            Button btnMap = (Button) findViewById(R.id.btnMap);
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MenuActivity.this, GoogleTest.class);
                    startActivity(intent);
                }
            });
        }

        public boolean isServicesOK(){
            Log.d(TAG, "isServicesOK: checking google services version");

            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MenuActivity.this);

            if(available == ConnectionResult.SUCCESS){
                //everything is fine and the user can make map requests
                Log.d(TAG, "isServicesOK: Google Play Services is working");
                return true;
            }
            else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                //an error occured but we can resolve it
                Log.d(TAG, "isServicesOK: an error occured but we can fix it");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MenuActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            }else{
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        if(isServicesOK()){
            init();
        }
    }

    //주소찾기
    public void searchAdd(View view)
    {

        Intent intent = new Intent(this, searchAddActivity.class);
        startActivity(intent);
    }

    //즐겨찾기
    public void favorite(View view)
    {
        Intent intent = new Intent(this, FavorActivity.class);
        startActivity(intent);
    }

        //돌아가기
        public void backMain(View view)
        {
            finish();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
}
