package com.activedistribution.presenter.login;

import com.activedistribution.R;
import com.activedistribution.model.LoginData;
import com.activedistribution.retrofitManager.RetrofitHandler;
import com.activedistribution.utils.Constants;
import com.activedistribution.utils.MainApplication;
import com.activedistribution.utils.Utils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginApiCall implements LoginInteractor {

    @Override
    public void doLogin(@NotNull String email, @NotNull String password, @NotNull String device_id, @NotNull String device_type,Double latitude,Double longitude, loginResponse loginSucess) {
        Call<LoginData> call = RetrofitHandler
                .getInstance()
                .getApi()
                .doLogin(email,password,device_id,device_type,latitude,longitude);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if (response.code() == Constants.STATUS_OK) {
                    loginSucess.onResponseSuccess(response.body());
                } else if(response.code()==Constants.UNAUTHORIZED) {
                    loginSucess.onUnauthorised(Utils.onResoinseError(response));
                }
                else {
                    loginSucess.onResponseFailure(Utils.onResoinseError(response));
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                loginSucess.onResponseFailure(MainApplication.getInstance().getString(R.string.on_dailure_error));
            }
        });
    }
}
