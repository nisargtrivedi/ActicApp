package com.activedistribution.presenter.changePassword

interface changePasswordPresenterInterface {

    fun checkValidation(oldPassword: String, newPassword: String, confirmPassword: String,token:String)
}
