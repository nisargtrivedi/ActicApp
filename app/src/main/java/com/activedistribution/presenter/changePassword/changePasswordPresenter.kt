package com.activedistribution.presenter.changePassword

import com.activedistribution.model.NotificationStatusData
import com.activedistribution.view.fragments.ChangePasswordFragment

class changePasswordPresenter(changePasswordFragment: ChangePasswordFragment) : changePasswordPresenterInterface,ChangePasswordInteractor.changePasswordSuceess {

    internal var changePasswordInterface: ChangePasswordInterface
    internal var changePasswordInteractor:ChangePasswordInteractor

    init {
        this.changePasswordInterface = changePasswordFragment
        this.changePasswordInteractor = ChangePasswordApiCall()
    }

    override fun checkValidation(oldPassword: String, newPassword: String, confirmPassword: String,token:String) {
        if (oldPassword.isEmpty()) {
            changePasswordInterface.onError("Please enter old password.")
        } else if (oldPassword.length < 8) {
            changePasswordInterface.onError("Old Password must be of at least 8 characters.")
        } else if (newPassword.isEmpty()) {
            changePasswordInterface.onError("Please enter new password.")
        } else if (newPassword.length < 8) {
            changePasswordInterface.onError("New Password must be of at least 8 characters.")
        } else if (confirmPassword.isEmpty()) {
            changePasswordInterface.onError("Please enter confirm password.")
        } else if (confirmPassword.length < 8) {
            changePasswordInterface.onError("Confirm Password must be of at least 8 characters.")
        } else if(!newPassword.contentEquals(confirmPassword)) {
            changePasswordInterface.onError("New Password and confirm password do not matched.")
        } else {
            changePasswordInterface.onShowProgress()
            changePasswordInteractor.changePassword(oldPassword,newPassword,token,this)
        }
    }

    override fun onResponseSuccess(data: NotificationStatusData?) {
        changePasswordInterface.onHideProgress()
        changePasswordInterface.onSuccess(data!!)
    }

    override fun onResponseFailure(message: String?) {
        changePasswordInterface.onHideProgress()
        changePasswordInterface.onError(message!!)
    }

    override fun onUnauthorised(message: String?) {
        changePasswordInterface.onHideProgress()
        changePasswordInterface.onUnauthorised(message!!)
    }

}
