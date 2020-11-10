package com.activedistribution.presenter.forgotPassword

import com.activedistribution.model.NotificationStatusData

interface forgotPasswordInterface {

    fun onSuccess(message: NotificationStatusData)

    fun onError(error: String)

    fun onShowProgress()

    fun onHideProgress()
}
