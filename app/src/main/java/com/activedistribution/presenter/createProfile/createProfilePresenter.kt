package com.activedistribution.presenter.createProfile

import com.activedistribution.model.CreateProfileData
import com.activedistribution.utils.Utils.getRequestBodyParam
import com.activedistribution.view.activities.CreateProfileActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

class createProfilePresenter(createProfileActivity: CreateProfileActivity) : createProfilePresenterInterface,createProfileInteractor.createProfileSuccess {


    internal var createProfileInterface: createProfileInterface
    internal var interactor : createProfileInteractor

    init {
        this.createProfileInterface = createProfileActivity
        this.interactor = createProfileApiCall()
    }

    override fun checkValidation(firstName: String, lastName: String, phone: String, address: String, zipcode: String, dob: String, userImage: File?, idProof: File?,updated_on:String,remember_token:String) {
        if (userImage == null) {
            createProfileInterface.onError("Please enter user picture.")
        } else if (firstName.isEmpty()) {
            createProfileInterface.onError("Please enter first name.")
        } else if (lastName.isEmpty()) {
            createProfileInterface.onError("Please enter last name.")
        } else if (phone.isEmpty()) {
            createProfileInterface.onError("Please enter phone number.")
        } else if(phone.length<10) {
            createProfileInterface.onError("Phone number must be of at least 10 characters")
        } else {
            createProfileInterface.onShowProgress()
            val hashMap:HashMap<String,RequestBody> = HashMap<String,RequestBody>()
            hashMap.put("firstname", getRequestBodyParam(firstName))
            hashMap.put("lastname", getRequestBodyParam(lastName))
            hashMap.put("phone", getRequestBodyParam(phone))
            hashMap.put("address", getRequestBodyParam(address))
            hashMap.put("zipcode", getRequestBodyParam(zipcode))
            hashMap.put("dob", getRequestBodyParam(dob))
            hashMap.put("updated_at", getRequestBodyParam(updated_on))

            if (userImage != null) {
                val body = RequestBody.create(MediaType.parse("multipart/form-data"), userImage)
                hashMap.put("image\"; filename=\"" + userImage.getName(), body)
            }
            if (idProof != null) {
                val body = RequestBody.create(MediaType.parse("multipart/form-data"), idProof)
                hashMap.put("attach_id\"; filename=\"" + idProof.getName(), body)
            }
            interactor.onCreateProfile(hashMap,this,remember_token)
        }
    }

    override fun onResponseSuccess(response: CreateProfileData?) {
        createProfileInterface.onHideProgress()
        createProfileInterface.onSuccess(response!!)
    }

    override fun onResponseFailure(message: String?) {
        createProfileInterface.onHideProgress()
        createProfileInterface.onError(message!!)
    }

    override fun onUnauthorised(message: String?) {
        createProfileInterface.onHideProgress()
        createProfileInterface.onUnauthorized(message!!)
    }
}
