package com.activedistribution.presenter.EditProfile;

import com.activedistribution.model.CreateProfileData;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import okhttp3.RequestBody;

public interface EditProfileInteractor {

    interface editProfileSuccess {
        void onResponseSuccess(CreateProfileData message);
        void onUnauthorised(String message);
        void onResponseFailure(String message);
    }

    void updateProfile(@NotNull HashMap<String, RequestBody> hashMap, @NotNull editProfileSuccess success, @NotNull String token);

}
