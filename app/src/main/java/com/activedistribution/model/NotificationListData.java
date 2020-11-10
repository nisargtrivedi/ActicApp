package com.activedistribution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class NotificationListData {


    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data {
        @Expose
        @SerializedName("updated_at")
        public String updated_at;
        @Expose
        @SerializedName("created_at")
        public String created_at;
        @Expose
        @SerializedName("message")
        public String message;
        @Expose
        @SerializedName("job_id")
        public int job_id;
        @Expose
        @SerializedName("user_id")
        public int user_id;
        @Expose
        @SerializedName("id")
        public int id;
    }
}
