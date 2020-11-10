package com.activedistribution.presenter.changePassword

import com.activedistribution.model.NotificationStatusData

interface ChangePasswordInterface {

    fun onSuccess(data: NotificationStatusData)
    fun onError(error: String)
    fun onUnauthorised(error: String)
    fun onShowProgress()
    fun onHideProgress()
}
