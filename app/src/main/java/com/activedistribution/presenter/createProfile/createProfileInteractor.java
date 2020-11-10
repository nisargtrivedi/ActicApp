package com.activedistribution.presenter.createProfile;

import com.activedistribution.model.CreateProfileData;
import com.activedistribution.model.SignUpData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.HashMap;

import okhttp3.RequestBody;

public interface createProfileInteractor  {

     interface createProfileSuccess {
         void onResponseSuccess(CreateProfileData response);
         void onUnauthorised(String response);
         void onResponseFailure(String message);
     }

  //  void onCreateProfile(@NotNull String firstName, @NotNull String lastName, @NotNull String phone, @NotNull String address, @NotNull String zipcode, @NotNull String dob, @NotNull File userImage, @Nullable File idProof, @NotNull String updated_on,createProfileSuccess createProfileSuccess);
    void onCreateProfile(HashMap<String, RequestBody> hashMap, createProfileSuccess createProfileSuccess,String remember_token);
}
