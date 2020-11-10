package com.activedistribution.presenter.forgotPassword

import android.util.Patterns
import com.activedistribution.model.NotificationStatusData
import com.activedistribution.view.activities.ForgotPasswordActivity

class forgotPasswordPresenter(forgotPasswordActivity: ForgotPasswordActivity) : forgotPasswordPresenterInterface, ForgotPasswordInteractor.forgotPasswordSuccess {


    internal var forgotPasswordInterface: forgotPasswordInterface
    internal var forgotPasswordInteractor : ForgotPasswordInteractor

    init {
        this.forgotPasswordInterface = forgotPasswordActivity
        this.forgotPasswordInteractor = ForgotPasswordApiCall()
    }

    override fun checkValidation(email: String) {
        if (email.isEmpty()) {
            forgotPasswordInterface.onError("Please enter email.")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            forgotPasswordInterface.onError("Please enter valid email address.")
        } else {
            forgotPasswordInteractor.forgotPassword(email,this)
        }
    }

    override fun onResponseSuccess(data: NotificationStatusData?) {
        forgotPasswordInterface.onHideProgress()
        forgotPasswordInterface.onSuccess(data!!)
    }

    override fun onResponseFailure(error: String?) {
        forgotPasswordInterface.onHideProgress()
        forgotPasswordInterface.onError(error!!)
    }

    override fun onUnauthorised(message: String?) {
        forgotPasswordInterface.onHideProgress()
        forgotPasswordInterface.onError(message!!)
    }
}
