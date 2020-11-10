package com.activedistribution.presenter.ChangeNotification;

import com.activedistribution.model.NotificationStatusData;

public interface NotificationInteractor {

    interface notificationResponse {
    void onResponseSuccess(NotificationStatusData data);
    void onResponseFailure(String message);
    void onUnauthorize(String message);
    void onShowProgress();
    void onHideProgress();
    }

    void changeNotificationStatus(String status,String token,notificationResponse response);
}
