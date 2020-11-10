package com.activedistribution.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.activedistribution.R
import com.activedistribution.model.ContactUsData
import com.activedistribution.model.ProfileData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.Utils
import com.activedistribution.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsFragment : BaseFragment(), View.OnClickListener {

    private var iv_back: ImageView? = null
    private var companyNameET: EditText? = null
    private var firstNameET: EditText? = null
    private var lastNameET: EditText? = null
    private var phoneET: EditText? = null
    private var messageET: EditText? = null
    private var submitBT: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_contact_us, container, false)
        initUI(v)
        return v
    }

    private fun initUI(v: View) {
        iv_back = v.findViewById(R.id.iv_back)
        submitBT = v.findViewById(R.id.submitBT)
        companyNameET = v.findViewById(R.id.companyNameET)
        firstNameET = v.findViewById(R.id.firstNameET)
        lastNameET = v.findViewById(R.id.lastNameET)
        phoneET = v.findViewById(R.id.phoneET)
        messageET = v.findViewById(R.id.messageET)
        iv_back!!.setOnClickListener(this)
        submitBT!!.setOnClickListener(this)
        companyNameET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        firstNameET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        lastNameET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        phoneET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        messageET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        val profileData = baseActivity.store!!.getProfileData()
        setData(profileData)
    }

    private fun setData(profileData: ProfileData) {
        firstNameET!!.setText(profileData.firstname)
        lastNameET!!.setText(profileData.lastname)
        phoneET!!.setText(profileData.phone)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> baseActivity.onBackPressed()
            R.id.submitBT -> {
                val compName = companyNameET!!.text.toString().trim { it <= ' ' }
                val firstName = firstNameET!!.text.toString().trim { it <= ' ' }
                val lastName = lastNameET!!.text.toString().trim { it <= ' ' }
                val phoneNo = phoneET!!.text.toString().trim { it <= ' ' }
                val message = messageET!!.text.toString().trim { it <= ' ' }
                if (compName.isEmpty()) {
                    baseActivity.showToast("Please enter company name.", false)
                } else if (firstName.isEmpty()) {
                    baseActivity.showToast("Please enter first name.", false)
                } else if (lastName.isEmpty()) {
                    baseActivity.showToast("Please enter last name.", false)
                } else if (phoneNo.isEmpty()) {
                    baseActivity.showToast("Please enter contact number.", false)
                } else if (message.isEmpty()) {
                    baseActivity.showToast("Please enter message.", false)
                } else {
                    submit(compName,firstName,lastName,phoneNo,message)
                }
            }
        }
    }

    private fun submit(compName: String, firstName: String, lastName: String, phoneNo: String, message: String) {
        baseActivity.showProgressDialog(baseActivity)
        val call = RetrofitHandler
                .getInstance()
                .api
                .contactUs(compName,firstName,lastName,phoneNo,message,baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<ContactUsData> {
            override fun onResponse(call: Call<ContactUsData>, response: Response<ContactUsData>) {
                baseActivity.hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {
                    baseActivity.store!!.setBoolean("edit_back",false)
                    baseActivity.showToast("Your message has been sent successfully",false)
                    startActivity(Intent(baseActivity, MainActivity::class.java))
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        baseActivity.goToLoginActivity()
                    } else {
                        baseActivity.showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<ContactUsData>, t: Throwable) {
                baseActivity.hideProgressDialog()
               // baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }
}
