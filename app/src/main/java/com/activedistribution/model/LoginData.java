package com.activedistribution.model;

import com.google.gson.annotations.SerializedName;

public class LoginData {


    @SerializedName("user_info")
    public ProfileData user_info;
    @SerializedName("message")
    public String message;

    /*public static class User_info {
        @SerializedName("updated_at")
        public String updated_at;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("notification_status")
        public String notification_status;
        @SerializedName("user_type")
        public String user_type;
        @SerializedName("device_type")
        public String device_type;
        @SerializedName("device_id")
        public String device_id;
        @SerializedName("attach_id")
        public String attach_id;
        @SerializedName("remember_token")
        public String remember_token;
        @SerializedName("email")
        public String email;
        @SerializedName("lastname")
        public String lastname;
        @SerializedName("firstname")
        public String firstname;
        @SerializedName("id")
        public int id;
    }*/
}
