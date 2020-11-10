package com.activedistribution.presenter.login;

import com.activedistribution.model.LoginData;

import org.jetbrains.annotations.NotNull;

public interface LoginInteractor {

    interface loginResponse {
        void onResponseSuccess(LoginData loginData);
        void onUnauthorised(String message);
        void onResponseFailure(String message);
    }

    void doLogin(@NotNull String email, @NotNull String password, @NotNull String device_id, @NotNull String device_type,Double latitude,Double longitude,loginResponse loginResponse);

}
