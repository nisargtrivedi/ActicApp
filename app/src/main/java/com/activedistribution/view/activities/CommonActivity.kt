package com.activedistribution.view.activities

import android.content.Intent
import android.os.Bundle
import com.activedistribution.R
import com.activedistribution.view.fragments.*
import com.google.android.gms.location.places.ui.PlaceAutocomplete

class CommonActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        store!!.setBoolean("edit_back",true)
        if (intent.extras.getString("change_password")!=null && intent.extras.getString("change_password").equals("change_password")){
            loadFragment1(ChangePasswordFragment(),R.id.common_frame)
        } else if (intent.extras.getString("edit")!=null && intent.extras.getString("edit").equals("edit")) {
            loadFragment1(EditProfileFragment(),R.id.common_frame)
        } else if(intent.extras.getString("notification")!=null && intent.extras.getString("notification").equals("notification")) {
            loadFragment1(NotificationsFragment(),R.id.common_frame)
        } else if(intent.extras.getString("NewRequest")!=null && intent.extras.getString("NewRequest").equals("NewRequest")) {
            loadFragment1(JobDetailFragment(),R.id.common_frame)
        } else if(intent.extras.getString("Current")!=null && intent.extras.getString("Current").equals("Current")) {
            loadFragment1(JobDetailFragment(),R.id.common_frame)
        } else if(intent.extras.getString("Completed")!=null && intent.extras.getString("Completed").equals("Completed")) {
            loadFragment1(JobDetailFragment(),R.id.common_frame)
        } else if(intent.extras.getString("contact_us")!=null && intent.extras.getString("contact_us").equals("contact_us")) {
            loadFragment1(ContactUsFragment(),R.id.common_frame)
        } else if(intent.extras.getString("about_us")!=null && intent.extras.getString("about_us").equals("about_us")) {
            loadFragment1(PrivacyFragment(),R.id.common_frame)
        } else if(intent.extras.getString("is_push")!=null && intent.extras.getString("is_push").equals("is_push")) {
            store!!.setBoolean("is_push",true)
            loadFragment1(JobDetailFragment(),R.id.common_frame)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data!=null) {
            var fragment = supportFragmentManager.findFragmentById(R.id.common_frame) as EditProfileFragment
            if(!store!!.getBoolean("places")) {
                fragment.setImage(data, requestCode)
            } else {
                try {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    if(place!=null) {
                        fragment.setPlace((place.address as String?)!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.common_frame)
        if(fragment is EditProfileFragment) {
            store!!.setBoolean("edit_back",true)
        } else if(fragment is NotificationsFragment) {
            store!!.setBoolean("edit_back",false)
        }
    }
}