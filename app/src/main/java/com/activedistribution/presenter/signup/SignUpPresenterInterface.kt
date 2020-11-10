package com.activedistribution.presenter.signup

interface SignUpPresenterInterface {

    fun checkValidation(email: String, password: String, confirmPassword: String,device_id:String,device_type:String)
}
