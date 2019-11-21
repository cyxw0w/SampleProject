package com.example.cyxw0w.andtest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class CenterPin extends AppCompatActivity
implements OnMapReadyCallback,
PlacesListener{

    Button button;

    private DatabaseReference mPostReference;
    //데이터 베이스용 변수
    Double Lat;
    Double Lon;

    double lat;
    double lon;

    double latP;
    double lonP;
    private static GoogleMap mMap ;

    List<Marker> previous_marker = null;
    String type;

    //private PlaceInfo mPlace;
    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    private static final String TAG = "CenterMap";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;

    LatLng Marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        previous_marker = new ArrayList<Marker>();

        final CharSequence[] items = {"음식점", "지하철역", "카페","술집"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //getFirebaseDatabase();

        Button button = (Button) findViewById(R.id.button_Show);    //showPlace함수 호출(주변 장소 검색)
        button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.setTitle("장소를 선택하세요")
                                .setItems(items,new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int index){
                                        switch (index){
                                            case 0:
                                                type = PlaceType.RESTAURANT;
                                                break;
                                            case 1:
                                                type = PlaceType.SUBWAY_STATION;
                                                break;
                                            case 2:
                                                type = PlaceType.CAFE;
                                                break;
                                            case 3:
                                                type = PlaceType.BAR;
                                                break;
                                        }

                                        Toast.makeText(getApplicationContext(),items[index], Toast.LENGTH_SHORT).show();

                                        mMap.clear();

                                        if(previous_marker != null)
                                            previous_marker.clear();

                                        new NRPlaces.Builder()
                                                .listener(CenterPin.this)
                                                .key("AIzaSyDV6wGAp4jgEuCs0fbSC6v8Wy79SZte55Y")
                                                .latlng(lat, lon)
                                                .radius(1000)
                                                .type(type)
                                                .build()
                                                .execute();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
        });
    }

    public void postFirebaseDatabase(boolean add){
        //데이터베이스 참조
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        //데이터를 삭제할건가 말건가(true는 추가, false면 추가안함)
        if(add){
            //FirebasePost클래스의 객체 post선언 (스트링 입력)
            FirebasePost post = new FirebasePost("",Lat, Lon);
            //post벨류값을 post의 toMap 함수 반환값
            postValues = post.toMap_pos();
        }
        childUpdates.put("/pos_list/" + Lat, postValues);
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
                    String[] info = {String.valueOf(get.Latitude), String.valueOf(get.Longitude)};
                    String Result = setTextLength(info[0],10) + setTextLength(info[1],10);
                    //주소 저장
                    arrayData.add(Result);
                    arrayIndex.add(key);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2] + info[3]);
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
//마커 클릭시
    public void markPick(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CenterPin.this);
        dialog.setTitle("위치 선택")
                .setMessage("해당 위치로 일정을 잡으시겠습니까?" + "\n")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double latParam = latP;
                        double lonParam = lonP;
                        Log.d(TAG, "moveCamera: Intent to: lat: " + latP + ", lng: " + lonP );
                        Intent intent = new Intent(CenterPin.this, editScheduleActivity.class);
                        intent.putExtra("latParam",latParam);        //latParam 이라는 이름으로 Lat전달
                        intent.putExtra("lonParam",lonParam);        //lonParam 이라는 이름으로 Lon전달
                        startActivity(intent);
                        Log.d(TAG, "moveCamera: Intent to: lat: " + latParam + ", lng: " + lonParam );
                        Toast.makeText(CenterPin.this, "위치 선택완료", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CenterPin.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }
    //즐겨찾기 저장
    public void favPlace(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CenterPin.this);
        dialog.setTitle("즐겨찾기 추가")
                .setMessage("즐겨찾기 추가하시겠습니까?" + "\n")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Lat = latP;
                        Lon = lonP;
                        //postFirebaseDatabase(true);
                        //getFirebaseDatabase();
                        Toast.makeText(CenterPin.this, "즐겨찾기 추가", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CenterPin.this, "취소", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Intent intent = getIntent();
        double latParam = intent.getDoubleExtra("latParam", 37.5655532);     //lat값 받음
        double lonParam = intent.getDoubleExtra("lonParam",126.9732132);     //lon값 받음
        lat = latParam;
        lon = lonParam;

        mMap = map;

        LatLng center = new LatLng(lat, lon);

        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(center);

        mMap.addMarker(markerOptions);
        moveCamera(center,14.0f, "");

        Toast.makeText(this, "생성완료", Toast.LENGTH_SHORT).show();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions markerOptions1 = new MarkerOptions();
                LatLng Center = new LatLng(point.latitude, point.longitude);
                markerOptions1.position(Center);
                Log.d(TAG, "moveCamera: Intent to: " + Center.latitude + ", lng: " + Center.longitude );

                //moveCamera(Center,15.0f,"");
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker point) {
                        Marker = point.getPosition();
                        latP = Marker.latitude;
                        lonP = Marker.longitude;
                        moveCamera(Marker,15.0f,"");
                        Log.d(TAG, "moveCamera: Intent to: lat: " + Marker.latitude + ", lng: " + Marker.longitude );
                        Log.d(TAG, "moveCamera: Intent to: " + latP + ", lng: " + lonP );

                        return true;
                    }
                });
            }
        });

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(CenterPin.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
        }
        hideSoftKeyboard();
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
        Log.i("PlacesAPI", "onPlacesSuccess()");
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {
                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(place.getVicinity());
                    Marker item = mMap.addMarker(markerOptions);
                    previous_marker.add(item);
                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }
}
