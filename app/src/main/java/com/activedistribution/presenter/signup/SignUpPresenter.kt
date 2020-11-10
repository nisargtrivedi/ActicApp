package com.activedistribution.presenter.signup

import android.util.Patterns
import com.activedistribution.R
import com.activedistribution.R.drawable.phone
import com.activedistribution.model.SignUpData
import com.activedistribution.utils.MainApplication

import com.activedistribution.view.activities.SignUpActivity

class SignUpPresenter(signUpActivity: SignUpActivity) : SignUpPresenterInterface,SignUpInteractor.onSignedUpSuccess  {


    internal var signUpInterface: SignUpInterface
    internal var signUpInteractor: SignUpInteractor

    init {
        this.signUpInterface = signUpActivity
        this.signUpInteractor = SignApiCall()
    }

    override fun checkValidation(email: String, password: String, confirmPassword: String,device_id:String,device_type:String) {
        if (email.isEmpty()) {
            signUpInterface.onError("Please enter email.")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpInterface.onError("Please enter valid email address.")
        } else if (password.isEmpty()) {
            signUpInterface.onError("Please enter password.")
        } else if (password.length < 8) {
            signUpInterface.onError("Password must be of at least 8 characters.")
        } else if (confirmPassword.isEmpty()) {
            signUpInterface.onError("Please enter password.")
        } else if (confirmPassword.length < 8) {
            signUpInterface.onError("Password must be of at least 8 characters.")
        } else if (password != confirmPassword) {
            signUpInterface.onError("Password and confirm password do not matched.")
        } else {
                signUpInterface.onShowProgress()
                signUpInteractor.onSignUpCall(email, password, confirmPassword,device_id,device_type,this)
        }
    }

    override fun onResponseSuccess(response: SignUpData?) {
        signUpInterface.onHideProgress()
        signUpInterface.onSuccess(response!!)
    }

    override fun onResponseFailure(message: String?) {
        signUpInterface.onHideProgress()
        signUpInterface.onError(message!!)
    }

    override fun onUnauthorised(message: String?) {
        signUpInterface.onHideProgress()
        signUpInterface.onError(message!!)
    }
}
