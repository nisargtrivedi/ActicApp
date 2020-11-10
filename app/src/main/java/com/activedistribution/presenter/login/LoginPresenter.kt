package com.activedistribution.presenter.login

import android.util.Patterns
import com.activedistribution.model.LoginData

import com.activedistribution.view.activities.LoginActivity

class LoginPresenter(loginActivity: LoginActivity) : LoginPresenterInterface,LoginInteractor.loginResponse {

    internal var loginInterface: LoginInterface
    internal var loginInteractor: LoginInteractor

    init {
        this.loginInterface = loginActivity
        this.loginInteractor = LoginApiCall()
    }


    override fun checkValidation(email: String, password: String,device_id:String,device_type:String,latitude:Double,longitude:Double) {
        if (email.isEmpty()) {
            loginInterface.onError("Please enter email.")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginInterface.onError("Please enter valid email address.")
        } else if (password.isEmpty()) {
            loginInterface.onError("Please enter password.")
        } else if (password.length < 8) {
            loginInterface.onError("Password must be of at least 8 characters.")
        } else if(latitude==null || longitude==null) {
           var latitude=0.0
           var longitude=0.0
        } else {
            loginInterface.onShowProgress()
            loginInteractor.doLogin(email,password,device_id,device_type,latitude,longitude,this)
        }
    }

    override fun onResponseSuccess(loginData: LoginData?) {
        loginInterface.onHideProgress()
        loginInterface.onSuccess(loginData!!)
    }

    override fun onResponseFailure(message: String?) {
        loginInterface.onHideProgress()
        loginInterface.onError(message!!)
    }

    override fun onUnauthorised(message: String?) {
        loginInterface.onHideProgress()
        loginInterface.onError(message!!)
    }

}
