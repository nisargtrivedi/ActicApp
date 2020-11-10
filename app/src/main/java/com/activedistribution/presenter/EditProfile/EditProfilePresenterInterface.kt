package com.activedistribution.presenter.EditProfile

import java.io.File

interface EditProfilePresenterInterface {

    fun checkValidation(firstName: String, lastName: String, phone: String, address: String, zipcode: String, dob: String,token:String,userImage:File,proof_image:File,updated_on:String)
}
