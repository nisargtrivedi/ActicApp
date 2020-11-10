package com.activedistribution.presenter.createProfile

import java.io.File

interface createProfilePresenterInterface {

    fun checkValidation(firstName: String, lastName: String, phone: String, address: String, zipcode: String, dob: String, userImage: File?, idProof: File?,updated_on:String,remember_token:String)
}
