package com.activedistribution.presenter.changePassword;

import com.activedistribution.model.NotificationStatusData;

public interface ChangePasswordInteractor {

    interface changePasswordSuceess {

        void onResponseSuccess(NotificationStatusData data);
        void onUnauthorised(String data);
        void onResponseFailure(String message);
    }

    void changePassword(String oldPassword, String newPassword,String token,changePasswordSuceess success);
}
