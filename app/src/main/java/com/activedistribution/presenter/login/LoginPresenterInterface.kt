package com.activedistribution.presenter.login

interface LoginPresenterInterface {

    fun checkValidation(email: String, password: String,device_id:String,device_type:String,latitude:Double,longitude:Double)
}
