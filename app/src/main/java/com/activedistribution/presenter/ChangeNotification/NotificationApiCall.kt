package com.activedistribution.presenter.ChangeNotification

import com.activedistribution.R
import com.activedistribution.model.NotificationStatusData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.MainApplication
import com.activedistribution.utils.Utils
import com.activedistribution.view.fragments.SettingFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationApiCall(settingFragment: SettingFragment) : NotificationInteractor {

    override fun changeNotificationStatus(status: String,token:String, success: NotificationInteractor.notificationResponse) {
        val call = RetrofitHandler
                .getInstance()
                .api
                .changeNotificationStatus(status,token)

        call.enqueue(object : Callback<NotificationStatusData> {
            override fun onResponse(call: Call<NotificationStatusData>, response: Response<NotificationStatusData>) {
                if (response.code() == Constants.STATUS_OK) {
                    success.onResponseSuccess(response.body())
                } else if(response.code()==Constants.UNAUTHORIZED) {
                    success.onUnauthorize(Utils.onResoinseError(response))
                } else {
                    success.onResponseFailure(Utils.onResoinseError(response))
                }
            }

            override fun onFailure(call: Call<NotificationStatusData>, t: Throwable) {
                success.onResponseFailure(MainApplication.getInstance().getString(R.string.on_dailure_error))
            }
        })
    }

}
