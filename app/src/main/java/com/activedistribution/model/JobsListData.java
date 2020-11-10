package com.activedistribution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class JobsListData implements Serializable {

    @Expose
    @SerializedName("job_count")
    public int job_count;
    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Locations implements Serializable {
        @Expose
        @SerializedName("longitude")
        public String longitude;
        @Expose
        @SerializedName("latitude")
        public String latitude;
        @Expose
        @SerializedName("location_id")
        public String location_id;
    }

    public static class Data {
        @Expose
        @SerializedName("locations")
        public ArrayList<Locations> locations;
        @Expose
        @SerializedName("notification_count")
        public String notification_count;
        @Expose
        @SerializedName("images")
        public String images;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("flyers")
        public Double flyers;
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("job_number")
        public String job_number;
        @Expose
        @SerializedName("job_id")
        public int job_id;
        @Expose
        @SerializedName("job_name")
        public String job_name;
        @Expose
        @SerializedName("firstname")
        public String firstname;
        @Expose
        @SerializedName("user_id")
        public String user_id;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
