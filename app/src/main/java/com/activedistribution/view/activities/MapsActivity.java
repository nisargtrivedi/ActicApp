package com.activedistribution.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.activedistribution.R;
import com.activedistribution.model.JobsListData;
import com.activedistribution.model.NotificationStatusData;
import com.activedistribution.retrofitManager.ApiInterface;
import com.activedistribution.retrofitManager.RetrofitHandler;
import com.activedistribution.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageView iv_back;

    String lat = "", lng = "";
    String jobID = "0";
    String token = "";
    String userID = "0";

    List<NotificationStatusData.Data> list = new ArrayList<>();
    ArrayList<JobsListData.Locations> data=new ArrayList<JobsListData.Locations>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        iv_back = findViewById(R.id.iv_back);
        mapFragment.getMapAsync(this);
        lat = getIntent().getStringExtra("Latitude");
        lng = getIntent().getStringExtra("Longitude");
        jobID = getIntent().getStringExtra("JobID");
        token = getIntent().getStringExtra("token");
        userID = getIntent().getStringExtra("userID");
        data= (ArrayList<JobsListData.Locations>) getIntent().getSerializableExtra("data");
        System.out.println("ON MAP JOBID===>" + jobID);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void loadData() {
        Call<NotificationStatusData> call = RetrofitHandler.getInstance().getApi().changeLocation(lat, lng, token, jobID,"0");
        call.enqueue(new Callback<NotificationStatusData>() {
            @Override
            public void onResponse(Call<NotificationStatusData> call, Response<NotificationStatusData> response) {
                System.out.println("RESPONSE=====>" + response.body().message);
               // System.out.println("RESPONSE=====>" + response.toString());
                if(!response.body().speed_notify.isEmpty()){
                    Toast.makeText(MapsActivity.this,response.body().speed_notify,Toast.LENGTH_LONG).show();
                }
                NotificationStatusData notificationStatusData = response.body();
                if (response.isSuccessful()) {

                    list.addAll(notificationStatusData.dataArrayList);

                    //Collections.reverse(list);

                    //Collections.reverse(list);
                    int lock = 0;
                    // Traversing through all the routes
                   // String oldDateTime="";
                    String oldDateTime="";
                    for (int i = 0; i < list.size(); i++) {
                        // Fetching all the points in i-th route
                        ArrayList<LatLng> points = new ArrayList<>();
                        PolylineOptions lineOptions = new PolylineOptions();
                        if (i == 0) {
                            if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                lineOptions.color(Color.parseColor("#0b24fb"));
                            else
                                lineOptions.color(Color.parseColor("#489fe5"));
                        } else if (i == 1) {
                            if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                lineOptions.color(Color.BLUE);
                            else
                                lineOptions.color(Color.parseColor("#3a86b0"));
                        } else if (i == 2) {
                            if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                lineOptions.color(Color.BLUE);
                            else
                                lineOptions.color(Color.parseColor("#7bed9d"));
                        } else if (i == 3) {
                            if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                lineOptions.color(Color.BLUE);
                            else
                                lineOptions.color(Color.parseColor("#d2ca6d"));
                        } else {
                            lineOptions.color(Color.GRAY);
                        }

                        System.out.println("i = location size=" + i + list.get(i).locations.size());
                        for (int j = 0; j < list.get(i).locations.size(); j++) {

                            double lat = 0, lng2 = 0;
                            double lng = 0, lat2 = 0;
                            double degree = 0;

                            LatLng position, position2 = null;

                                if(oldDateTime.isEmpty()) {
                                    System.out.println("CALLED===>"+(lock++));
                                    oldDateTime = list.get(i).locations.get(j).track_time;
                                }

                            System.out.println("VALUE OF I =" + i + "\n VALUE OF J =" + j + "\nID = " + list.get(i).locations.get(j).LocationID);

                            if (j == list.get(i).locations.size() - 1) {

                                lat = Double.parseDouble(list.get(i).locations.get(j).latitude);
                                lng = Double.parseDouble(list.get(i).locations.get(j).longitude);

                                lng2 = Double.parseDouble(list.get(i).locations.get(j).longitude);
                                lat2 = Double.parseDouble(list.get(i).locations.get(j).latitude);


                                position = new LatLng(lat, lng);
                                position2 = new LatLng(lat2, lng2);
                                points.add(position2);
                                points.add(position);
                                degree = bearingBetweenLocations(position, position2);
                            } else {
                                lat = Double.parseDouble(list.get(i).locations.get(j + 1).latitude);
                                lng = Double.parseDouble(list.get(i).locations.get(j + 1).longitude);

                                lng2 = Double.parseDouble(list.get(i).locations.get(j).longitude);
                                lat2 = Double.parseDouble(list.get(i).locations.get(j).latitude);

                                position = new LatLng(lat, lng);
                                position2 = new LatLng(lat2, lng2);
                                points.add(position2);
                                points.add(position);

                                degree = bearingBetweenLocations(position, position2);
                            }


                            //System.out.print("DEGREEEEEE=====>"+(int)degree);
                            degree = (int) degree;

                            if (j == list.get(i).locations.size() - 1) {
                                if (i == 0) {
                                    if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)).flat(true));
                                    else
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.default_three)).flat(true));
                                }
                                if (i == 1) {
                                    if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)).flat(true));
                                    else
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.default_four)).flat(true));
                                }
                                if (i == 2) {
                                    if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)).flat(true));
                                    else
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.default_five)).flat(true));
                                }
                                if (i == 3) {
                                    if (userID.equalsIgnoreCase(list.get(i).UserID + ""))
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)).flat(true));
                                    else
                                        mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.default_six)).flat(true));
                                }


                            }
                            else if(j==0){
                                if (degree >= 0 && degree <= 45) {
                                    System.out.println("DOWN");
                                    if (i == 0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_three)).flat(true));
                                        }
                                    }
                                    if (i == 1) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_four)).flat(true));

                                        }
                                    }
                                    if (i == 2) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));

                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_five)).flat(true));
                                        }
                                    }
                                    if (i == 3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_six)).flat(true));
                                        }
                                    }
                                } else if (degree >= 46 && degree <= 135) {
                                    System.out.println("LEFT");
                                    if(i==0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_three)).flat(true));
                                        }
                                    }
                                    if(i==1) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_four)).flat(true));
                                        }
                                    }
                                    if(i==2) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;

                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_five)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if(i==3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        } else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_six)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                } else if (degree >= 136 && degree <= 225) {
                                    System.out.println("UP");
                                    if(i==0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                        } else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_three)).flat(true));
                                        }
                                    }
                                    if(i==1){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_four)).flat(true));
                                        }
                                    }
                                    if(i==2){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_five)).flat(true));
                                        }
                                    }
                                    if(i==3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                        } else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_six)).flat(true));
                                        }
                                    }
                                } else if (degree >= 226 && degree <= 315) {
                                    System.out.println("RIGHT");
                                    if(i==0){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                        }else{

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_three)).flat(true));
                                        }}
                                    if(i==1){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                        }else{

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_four)).flat(true));
                                        }}
                                    if(i==2){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                        }else{

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_five)).flat(true));
                                        }}
                                    if(i==3){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                        }else{
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_six)).flat(true));
                                        }}
                                } else if (degree >= 316 && degree <= 360) {
                                    System.out.println("DOWN");
                                    if (i == 0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        }else {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_three)).flat(true));
                                        }
                                    }
                                    if (i == 1) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_four)).flat(true));
                                        }
                                    }
                                    if (i == 2) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        }else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_five)).flat(true));
                                        }
                                    }
                                    if (i == 3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                        } else {

                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_six)).flat(true));
                                        }
                                    }
                                }
                            }

                            else {
                                if (degree >= 0 && degree <= 45) {
                                    System.out.println("DOWN");
                                    if (i == 0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_three)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if (i == 1) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_four)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if (i == 2) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }

                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_five)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if (i == 3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_six)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                } else if (degree >= 46 && degree <= 135) {
                                    System.out.println("LEFT");
                                    if(i==0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_three)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if(i==1) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_four)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if(i==2) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_five)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if(i==3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        } else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.left_six)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                } else if (degree >= 136 && degree <= 225) {
                                    System.out.println("UP");
                                    if(i==0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        } else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_three)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if(i==1){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_four)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }

                                        }
                                    }
                                    if(i==2){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_five)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if(i==3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        } else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.up_six)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                } else if (degree >= 226 && degree <= 315) {
                                    System.out.println("RIGHT");
                                    if(i==0){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else{
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_three)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                    }}
                                    if(i==1){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else{
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_four)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                    }}
                                    if(i==2){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else{
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_five)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                    }}
                                    if(i==3){
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else{
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.right_six)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                    }}
                                } else if (degree >= 316 && degree <= 360) {
                                    System.out.println("DOWN");
                                    if (i == 0) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_three)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if (i == 1) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_four)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if (i == 2) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_five)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                    if (i == 3) {
                                        if (userID.equalsIgnoreCase(list.get(i).UserID + "")) {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        } else {
                                            if (getDate(list.get(i).locations.get(j).track_time, oldDateTime) > 3) {
                                                mMap.addMarker(new MarkerOptions().position(position2).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.down_six)).flat(true));
                                                oldDateTime=list.get(i).locations.get(j).track_time;
                                            }
                                        }
                                    }
                                }
//                                        else{
//                                            mMap.addMarker(new MarkerOptions().position(position).title(list.get(i).UserName).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)).flat(true));
//                                        }
                            }

                            System.out.println("POINT SIZE===>" + points.size());

                           // oldDateTime=list.get(i).locations.get(j).track_time;

                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.geodesic(true);
                        lineOptions.addAll(points);
                        points.clear();

                        lineOptions.width(2);

                        LatLngBounds.Builder builder;
                        Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                        if (lineOptions != null) {
                            mMap.addPolyline(lineOptions);
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(Double.parseDouble(list.get(0).locations.get(0).latitude), Double.parseDouble(list.get(0).locations.get(0).longitude)))
                                    .zoom(20f)
                                    .build();
                            mMap.setMaxZoomPreference(100f);
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        } else {
                            Log.d("onPostExecute", "without Polylines drawn");
                        }

                    }


                    // Drawing polyline in the Google Map for the i-th route


                }
            }

            @Override
            public void onFailure(Call<NotificationStatusData> call, Throwable t) {

            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        //bindData();
        // Add a marker in Sydney and move the camera


        // Polygon polygon = mMap.addPolygon(new PolygonOptions().);
        PolygonOptions lineOptions = new PolygonOptions();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear();
                double lat_i = 0.0;
                double longi_i = 0.0;
                 ArrayList<LatLng> latLngPl=new  ArrayList();
                for(int i=0;i<data.size();i++){
                    lat_i = lat_i + Double.parseDouble(data.get(i).latitude);
                    longi_i = longi_i + Double.parseDouble(data.get(i).longitude);
                    latLngPl.add(new LatLng(Double.parseDouble(data.get(i).latitude), Double.parseDouble(data.get(i).longitude)));
                }


                lineOptions.geodesic(true);
                lineOptions.strokeWidth(2F);
                lineOptions.strokeColor(Color.BLUE);
                lineOptions.fillColor(Color.parseColor("#90BCDDE9"));
                 lineOptions.addAll(latLngPl);
                mMap.addPolygon(lineOptions);
                loadData();
            }
        });


    }

    public double degreesToRadians(double degree) {
        return degree * Math.PI / 180.0;
    }

    public double radiansToDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }


    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    public long getDate(String d1,String d2){
        long l=0;
        System.out.println("DATE 1= "+d1+"\n DATE 2 = "+d2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse(d1);
            Date date2 = simpleDateFormat.parse(d2);

            l=printDifference(date1, date2);
            System.out.println("\nDifference =>"+l);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("---------NOT PARSE-----------");
        }

        return  l;
    }

    public long printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedMinutes;

    }

//    private void bindData(){
//        ArrayList<LatLng> points = new ArrayList<>();
//        PolylineOptions lineOptions = new PolylineOptions();
//
////        double lat = 40.722543;
////        double lng = -73.998585;
////
////        double lat3 = 40.7057;
////        double lng3 = -73.9964;
////
////        double lat2 = 40.7064;
////        double lng2 = -74.0094;
//
////        41.84268,-87.63640
////        41.81479,-87.63606
////        41.81889,-87.60756
////        41.84114,-87.61374
//
////        41.84549,-87.58010
//
////        41.80789,-87.57495
////        41.80481,-87.60756
//
//        double lat = 41.84268;
//        double lng = -87.63640;
//
//        double lat2 = 41.81479;
//        double lng2 = -87.63606;
//
//
//        double lat3 = 41.81889;
//        double lng3 = -87.60756;
//
//        double lat4 = 41.84114;
//        double lng4 = -87.61374;
//
//        double lat5 = 41.84549;
//        double lng5 = -87.58010;
//
//        double lat6 = 41.80789;
//        double lng6 = -87.57495;
//
//        double lat7 = 41.80481;
//        double lng7 = -87.60756;
//
//
//        LatLng position = new LatLng(lat, lng);
//        LatLng position2 = new LatLng(lat2, lng2);
//        LatLng position3 = new LatLng(lat3, lng3);
//        LatLng position4 = new LatLng(lat4, lng4);
//        LatLng position5 = new LatLng(lat5, lng5);
//        LatLng position6 = new LatLng(lat6, lng6);
//        LatLng position7 = new LatLng(lat7, lng7);
//
//
//        double degree= bearingBetweenLocations(position,position2);
//        System.out.print("DEGREEEEEE=====>"+(int)degree);
//        degree=(int)degree;
//
//        mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)).flat(true));
//
//        // mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        if (degree >= 0 && degree <= 45) {
//            System.out.println("DOWN");
//            //      mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//        else if(degree>=46 && degree<=135){
//            System.out.println("LEFT");
//            //       mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//        } else if (degree >= 136 && degree <= 225) {
//            System.out.println("UP");
//            //      mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//        } else if (degree >= 226 && degree <= 315) {
//            System.out.println("RIGHT");
//            //     mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//        } else if (degree >= 316 && degree <= 360) {
//            System.out.println("DOWN");
//            //     mMap.addMarker(new MarkerOptions().position(position).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//
//
//
//        degree= bearingBetweenLocations(position2,position3);
//
//
//        if (degree >= 0 && degree <= 45) {
//            System.out.println("DOWN");
//            //      mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//        else if(degree>=46 && degree<=135){
//            System.out.println("LEFT");
//            //        mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//        } else if (degree >= 136 && degree <= 225) {
//            System.out.println("UP");
//            //       mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//        } else if (degree >= 226 && degree <= 315) {
//            System.out.println("RIGHT");
//            //       mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//        } else if (degree >= 316 && degree <= 360) {
//            System.out.println("DOWN");
//            //       mMap.addMarker(new MarkerOptions().position(position2).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//
//
//        degree= bearingBetweenLocations(position3,position4);
//
//
//        if (degree >= 0 && degree <= 45) {
//            System.out.println("DOWN");
//            //      mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//        else if(degree>=46 && degree<=135){
//            System.out.println("LEFT");
//            //      mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//        } else if (degree >= 136 && degree <= 225) {
//            System.out.println("UP");
//            //     mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//        } else if (degree >= 226 && degree <= 315) {
//            System.out.println("RIGHT");
//            //     mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//        } else if (degree >= 316 && degree <= 360) {
//            System.out.println("DOWN");
//            //    mMap.addMarker(new MarkerOptions().position(position3).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//
//
//        degree= bearingBetweenLocations(position4,position5);
//
//
//        if (degree >= 0 && degree <= 45) {
//            System.out.println("DOWN");
//            //      mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//        else if(degree>=46 && degree<=135){
//            System.out.println("LEFT");
//            //    mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//        } else if (degree >= 136 && degree <= 225) {
//            System.out.println("UP");
//            //      mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//        } else if (degree >= 226 && degree <= 315) {
//            System.out.println("RIGHT");
//            //       mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//        } else if (degree >= 316 && degree <= 360) {
//            System.out.println("DOWN");
//            //       mMap.addMarker(new MarkerOptions().position(position4).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//
//        degree= bearingBetweenLocations(position5,position6);
//
//
//        if (degree >= 0 && degree <= 45) {
//            System.out.println("DOWN");
//            //      mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//        else if(degree>=46 && degree<=135){
//            System.out.println("LEFT");
//            //      mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//        } else if (degree >= 136 && degree <= 225) {
//            System.out.println("UP");
//            //      mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//        } else if (degree >= 226 && degree <= 315) {
//            System.out.println("RIGHT");
//            //     mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//        } else if (degree >= 316 && degree <= 360) {
//            System.out.println("DOWN");
//            //      mMap.addMarker(new MarkerOptions().position(position5).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//
//        degree= bearingBetweenLocations(position6,position7);
//
//
//        if (degree >= 0 && degree <= 45) {
//            System.out.println("DOWN");
//            //       mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position7).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//        else if(degree>=46 && degree<=135){
//            System.out.println("LEFT");
//            //       mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position7).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_left)).flat(true));
//        } else if (degree >= 136 && degree <= 225) {
//            System.out.println("UP");
//            //       mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position7).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_top)).flat(true));
//        } else if (degree >= 226 && degree <= 315) {
//            System.out.println("RIGHT");
//            //      mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position7).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_right)).flat(true));
//        } else if (degree >= 316 && degree <= 360) {
//            System.out.println("DOWN");
//            //     mMap.addMarker(new MarkerOptions().position(position6).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//            mMap.addMarker(new MarkerOptions().position(position7).title("demo").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bottom)).flat(true));
//        }
//
//
//        points.add(position);
//        points.add(position2);
//        points.add(position3);
//        points.add(position4);
//        points.add(position5);
//        points.add(position6);
//        points.add(position7);
//
//        lineOptions.addAll(points);
//        lineOptions.width(2);
//        lineOptions.geodesic(true);
//        lineOptions.color(Color.BLUE);
//
//        if(lineOptions != null) {
//            mMap.addPolyline(lineOptions);
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(41.84268,-87.63640))
//                    .zoom(5)
//                    .build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//        }
//        else {
//            Log.d("onPostExecute","without Polylines drawn");
//        }
////
//
//
//    }


}
