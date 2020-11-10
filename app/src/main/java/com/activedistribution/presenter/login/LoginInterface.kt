package com.activedistribution.presenter.login

import com.activedistribution.model.LoginData

interface LoginInterface {

    fun onError(error: String)

    fun onSuccess(response: LoginData)

    fun onShowProgress()

    fun onHideProgress()
}
