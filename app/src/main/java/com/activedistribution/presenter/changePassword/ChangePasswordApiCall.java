package com.activedistribution.presenter.changePassword;

import com.activedistribution.R;
import com.activedistribution.model.NotificationStatusData;
import com.activedistribution.retrofitManager.RetrofitHandler;
import com.activedistribution.utils.Constants;
import com.activedistribution.utils.MainApplication;
import com.activedistribution.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordApiCall implements ChangePasswordInteractor {

    @Override
    public void changePassword(String oldPassword, String newPassword, String token, changePasswordSuceess success) {
        Call<NotificationStatusData> call = RetrofitHandler
                .getInstance()
                .getApi()
                .changePassword(oldPassword,newPassword,token);

        call.enqueue(new Callback<NotificationStatusData>() {
            @Override
            public void onResponse(Call<NotificationStatusData> call, Response<NotificationStatusData> response) {
                if (response.code() == Constants.STATUS_OK) {
                    success.onResponseSuccess(response.body());
                } else if(response.code()==Constants.UNAUTHORIZED) {
                    success.onUnauthorised(Utils.onResoinseError(response));
                } else {
                    success.onResponseFailure(Utils.onResoinseError(response));
                }
            }

            @Override
            public void onFailure(Call<NotificationStatusData> call, Throwable t) {
                success.onResponseFailure(MainApplication.getInstance().getString(R.string.on_dailure_error));
            }
        });
    }
}
