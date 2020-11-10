package com.activedistribution.presenter.signup

import com.activedistribution.model.SignUpData

interface SignUpInterface {

    fun onSuccess(signUpData: SignUpData)

    fun onError(error: String)

    fun onShowProgress()

    fun onHideProgress()
}
