package com.activedistribution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobDetail {


    @Expose
    @SerializedName("data")
    public Data data;

    public static class Data {
        @Expose
        @SerializedName("locations")
        public List<Locations> locations;
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
        @SerializedName("firstname")
        public String firstname;
        @Expose
        @SerializedName("user_id")
        public int user_id;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Locations {
        @Expose
        @SerializedName("longitude")
        public String longitude;
        @Expose
        @SerializedName("latitude")
        public String latitude;
        @Expose
        @SerializedName("location_id")
        public int location_id;
    }
}
