package com.activedistribution.presenter.EditProfile

import com.activedistribution.model.CreateProfileData
import com.activedistribution.presenter.createProfile.createProfileInterface
import com.activedistribution.utils.Utils
import com.activedistribution.view.fragments.EditProfileFragment
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

class EditProfilePresenter(editProfileFragment: EditProfileFragment) : EditProfilePresenterInterface, EditProfileInteractor.editProfileSuccess {


    internal var editProfileInterface: EditProfileInterface
    internal var editProfileInteractor : EditProfileInteractor

    init {
        this.editProfileInterface = editProfileFragment
        this.editProfileInteractor = EditProfileApiCall()
    }

    override fun checkValidation(firstName: String, lastName: String, phone: String, address: String, zipcode: String, dob: String,token:String,userImage:File,proof_image:File,updated_on:String) {
        if (firstName.isEmpty()) {
            editProfileInterface.onError("Please enter first name.")
        } else if (lastName.isEmpty()) {
            editProfileInterface.onError("Please enter last name.")
        } else if (phone.isEmpty()) {
            editProfileInterface.onError("Please enter phone number.")
        } else if (phone.length < 10) {
            editProfileInterface.onError("Phone number must be of at least 10 characters")
        }  else if(userImage==null) {
            editProfileInterface.onError("You cannot leave user image empty")
        } /*else if(proof_image==null) {
            editProfileInterface.onError("You cannot leave id proof image empty")
        }*/ else {
            editProfileInterface.onShowProgress()
            val hashMap:HashMap<String, RequestBody> = HashMap<String, RequestBody>()
            hashMap.put("firstname", Utils.getRequestBodyParam(firstName))
            hashMap.put("lastname", Utils.getRequestBodyParam(lastName))
            hashMap.put("phone", Utils.getRequestBodyParam(phone))
            hashMap.put("address", Utils.getRequestBodyParam(address))
            hashMap.put("zipcode", Utils.getRequestBodyParam(zipcode))
            hashMap.put("dob", Utils.getRequestBodyParam(dob))
            hashMap.put("updated_at", Utils.getRequestBodyParam(updated_on))

            if (userImage != null) {
                val body = RequestBody.create(MediaType.parse("multipart/form-data"), userImage)
                hashMap.put("image\"; filename=\"" + userImage.getName(), body)
            }
            if(proof_image!=null) {
                if (proof_image != null) {
                    val body = RequestBody.create(MediaType.parse("multipart/form-data"), proof_image)
                    hashMap.put("attach_id\"; filename=\"" + proof_image.getName(), body)
                }
            }
            editProfileInteractor.updateProfile(hashMap,this,token)
        }
    }


    override fun onResponseSuccess(response: CreateProfileData?) {
        editProfileInterface.onHideProgress()
        editProfileInterface.onSuccess(response!!)
    }

    override fun onResponseFailure(message: String?) {
        editProfileInterface.onHideProgress()
        editProfileInterface.onError(message!!)
    }

    override fun onUnauthorised(message: String?) {
        editProfileInterface.onHideProgress()
        editProfileInterface.onUnauthorised(message!!)
    }
}
