package com.activedistribution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileData implements Serializable {


    @Expose
    @SerializedName("updated_at")
    public String updated_at;
    @Expose
    @SerializedName("created_at")
    public String created_at;
    @Expose
    @SerializedName("notification_status")
    public int notification_status;
    @Expose
    @SerializedName("user_type")
    public String user_type;
    @Expose
    @SerializedName("device_type")
    public String device_type;
    @Expose
    @SerializedName("device_id")
    public String device_id;
    @Expose
    @SerializedName("attach_id")
    public String attach_id;
    @Expose
    @SerializedName("dob")
    public String dob;
    @Expose
    @SerializedName("zipcode")
    public String zipcode;
    @Expose
    @SerializedName("address")
    public String address;
    @Expose
    @SerializedName("phone")
    public String phone;
    @Expose
    @SerializedName("remember_token")
    public String remember_token;
    @Expose
    @SerializedName("email")
    public String email;
    @Expose
    @SerializedName("image")
    public String image;
    @Expose
    @SerializedName("lastname")
    public String lastname;
    @Expose
    @SerializedName("firstname")
    public String firstname;
    @Expose
    @SerializedName("id")
    public int id;
}
