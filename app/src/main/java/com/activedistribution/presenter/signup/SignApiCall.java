package com.activedistribution.presenter.signup;

import com.activedistribution.R;
import com.activedistribution.model.SignUpData;
import com.activedistribution.retrofitManager.RetrofitHandler;
import com.activedistribution.utils.Constants;
import com.activedistribution.utils.MainApplication;
import com.activedistribution.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignApiCall implements SignUpInteractor {


    @Override
    public void onSignUpCall(String email, String password, String confirmpassword,String device_id,String device_type, onSignedUpSuccess signedUpSuccess) {
        Call<SignUpData> call = RetrofitHandler
                .getInstance()
                .getApi()
                .doSignUp(email,password,device_id,device_type);

        call.enqueue(new Callback<SignUpData>() {
            @Override
            public void onResponse(Call<SignUpData> call, Response<SignUpData> response) {
                if (response.code() == Constants.STATUS_OK){
                    signedUpSuccess.onResponseSuccess(response.body());
                } else if(response.code()==Constants.UNAUTHORIZED) {
                    signedUpSuccess.onUnauthorised(Utils.onResoinseError(response));
                } else {
                    signedUpSuccess.onResponseFailure(Utils.onResoinseError(response));
                }
            }

            @Override
            public void onFailure(Call<SignUpData> call, Throwable t) {
                signedUpSuccess.onResponseFailure(MainApplication.getInstance().getString(R.string.on_dailure_error));
            }
        });
    }
}
