package com.activedistribution.retrofitManager;

import com.activedistribution.model.AcceptRejectData;
import com.activedistribution.model.ContactUsData;
import com.activedistribution.model.CreateProfileData;
import com.activedistribution.model.JobDetail;
import com.activedistribution.model.JobsListData;
import com.activedistribution.model.LoginData;
import com.activedistribution.model.NotificationListData;
import com.activedistribution.model.NotificationStatusData;
import com.activedistribution.model.ProfileData;
import com.activedistribution.model.SignUpData;
import com.google.gson.JsonObject;

import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<LoginData> doLogin(@Field("email") String email,
                            @Field("password") String password,
                            @Field("device_id") String device_id,
                            @Field("device_type") String device_type,
                            @Header("latitude") Double latitude,
                            @Header("longitude") Double longitude);

    @FormUrlEncoded
    @POST("changeLocation")
    Call<NotificationStatusData> changeLocation(@Field("latitude") String latitude, @Field("longitude") String longitude, @Header("token") String token,@Field("job_id") String JobID,@Field("speed") String speed);

    @FormUrlEncoded
    @POST("signUp")
    Call<SignUpData> doSignUp(@Field("email") String email,
                              @Field("password") String password,
                              @Field("device_id") String device_id,
                              @Field("device_type") String device_type);


    @Multipart
    @POST("createProfile")
    Call<CreateProfileData> createProfile(@Header("token") String token, @PartMap HashMap<String , RequestBody> hashMap);

    @Multipart
    @POST("createProfile")
    Call<CreateProfileData> updateProfile(@Header("token") String token, @PartMap HashMap<String , RequestBody> hashMap);


    @FormUrlEncoded
    @POST("changeNotification")
    Call<NotificationStatusData> changeNotificationStatus(@Field("status") String status,@Header("token") String token);

    @FormUrlEncoded
    @POST("acceptRejectJobs")
    Call<AcceptRejectData> acceptRejectJobs(@Field("request_id") String request_id, @Field("status") String status, @Header("token") String token);


    @GET("getJobDetail/{job_id}")
    Call<JobDetail> getJobDetail(@Path(value = "job_id", encoded = true) String job_id, @Header("token") String token);

    @FormUrlEncoded
    @POST("changePassword")
    Call<NotificationStatusData> changePassword(@Field("oldPassword") String oldPassword,
                            @Field("confirmPassword") String confirmPassword,
                            @Header("token") String token);

    @FormUrlEncoded
    @POST("contactInfo")
    Call<ContactUsData> contactUs(@Field("companyname") String companyname,
                                  @Field("firstname") String firstname,
                                  @Field("lastname") String lastname,
                                  @Field("phone") String phone,
                                  @Field("message") String message,
                                  @Header("token") String token);

    @GET("viewProfile")
    Call<ProfileData> getProfile(@Header("token") String token);


    @GET("getNotifications/{page}")
    Call<NotificationListData> getNotificationsList(@Path(value = "page", encoded = true) String page,
                                                    @Header("token") String token);

    @GET("getJobs/new_job/{page}")
    Call<JobsListData> getNewJobsList(@Path(value = "page", encoded = true) String page,
                                   @Header("token") String token);

    @GET("getJobs/current_job/{page}")
    Call<JobsListData> getCurrentJobsList(@Path(value = "page", encoded = true) String page,
                                      @Header("token") String token);

    @GET("getJobs/completed_job/{page}")
    Call<JobsListData> getCompletedJobsList(@Path(value = "page", encoded = true) String page,
                                      @Header("token") String token);

    @FormUrlEncoded
    @POST("forgotpassword")
    Call<NotificationStatusData> forgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("changeLocation")
    Call<JsonObject> getMapData(@Field("job_id") String JobID,@Field("latitude") String lat,
                                @Field("longitude") String lng,@Header("token") String token);

    @POST("logout")
    Call<NotificationStatusData> logout(@Header("token") String token);
}
