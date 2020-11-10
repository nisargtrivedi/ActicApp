package com.activedistribution.presenter.createProfile

import com.activedistribution.model.CreateProfileData
import java.net.CacheResponse

interface createProfileInterface {

    fun onSuccess(response: CreateProfileData)

    fun onError(error: String)

    fun onUnauthorized(error: String)

    fun onShowProgress()

    fun onHideProgress()
}
