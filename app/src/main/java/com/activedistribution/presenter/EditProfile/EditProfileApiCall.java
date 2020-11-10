package com.activedistribution.presenter.EditProfile;

import com.activedistribution.R;
import com.activedistribution.model.CreateProfileData;
import com.activedistribution.retrofitManager.RetrofitHandler;
import com.activedistribution.utils.Constants;
import com.activedistribution.utils.MainApplication;
import com.activedistribution.utils.Utils;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileApiCall implements EditProfileInteractor {

    @Override
    public void updateProfile(@NotNull HashMap<String, RequestBody> hashMap, @NotNull editProfileSuccess success, @NotNull String token) {
        Call<CreateProfileData> call = RetrofitHandler
                .getInstance()
                .getApi()
                .updateProfile(token,hashMap);

        call.enqueue(new Callback<CreateProfileData>() {
            @Override
            public void onResponse(Call<CreateProfileData> call, Response<CreateProfileData> response) {
                if (response.code() == Constants.STATUS_OK){
                    success.onResponseSuccess(response.body());
                }else if(response.code()==Constants.UNAUTHORIZED) {
                    success.onUnauthorised((Utils.onResoinseError(response)));
                } else {
                    success.onResponseFailure(Utils.onResoinseError(response));
                }
            }

            @Override
            public void onFailure(Call<CreateProfileData> call, Throwable t) {
                success.onResponseFailure(MainApplication.getInstance().getString(R.string.on_dailure_error));
            }
        });
    }
}