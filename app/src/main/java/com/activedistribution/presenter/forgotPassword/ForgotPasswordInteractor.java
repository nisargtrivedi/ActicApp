package com.activedistribution.presenter.forgotPassword;

import com.activedistribution.model.NotificationStatusData;

import org.jetbrains.annotations.NotNull;

public interface ForgotPasswordInteractor {

    interface forgotPasswordSuccess {
        void onResponseSuccess(NotificationStatusData message);
        void onUnauthorised(String message);
        void onResponseFailure(String error);
    }

    void forgotPassword(@NotNull String email,forgotPasswordSuccess success);
}
