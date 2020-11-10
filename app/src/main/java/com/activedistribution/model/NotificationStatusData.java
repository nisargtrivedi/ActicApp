package com.activedistribution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationStatusData {


    @Expose
    @SerializedName("message")
    public String message;

    @SerializedName("speed_notify")
    public String speed_notify;

    @SerializedName("data")
    public ArrayList<Data> dataArrayList=new ArrayList<>();

    public class Data{
        @SerializedName("uid")
        public int UserID=0;

        @SerializedName("name")
        public String UserName="";

        @SerializedName("locations")
        public ArrayList<Locations> locations=new ArrayList<>();


    }
    public class Locations{

        @SerializedName("location_id")
        public int LocationID=0;

        @SerializedName("latitude")
        public String latitude="";

        @SerializedName("longitude")
        public String longitude="";

        @SerializedName("track_time")
        public String track_time="";
    }
}
