package com.activedistribution.presenter.EditProfile

import com.activedistribution.model.CreateProfileData

interface EditProfileInterface {

    fun onSuccess(message: CreateProfileData)

    fun onError(error: String)

    fun onUnauthorised(error: String)

    fun onShowProgress()

    fun onHideProgress()
}
