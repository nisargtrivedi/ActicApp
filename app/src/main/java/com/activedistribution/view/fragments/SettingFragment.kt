package com.activedistribution.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.NotificationStatusData
import com.activedistribution.model.ProfileData
import com.activedistribution.presenter.ChangeNotification.NotificationApiCall
import com.activedistribution.presenter.ChangeNotification.NotificationInteractor
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.Utils
import com.activedistribution.view.activities.CommonActivity
import com.suke.widget.SwitchButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingFragment : BaseFragment(), View.OnClickListener,NotificationInteractor.notificationResponse {


    var notification_btn: SwitchButton? = null
    var changePasswordTV: TextView? = null
    var rateAppTV: TextView? = null
    var shareAppTV: TextView? = null
    var aboutUsTV: TextView? = null
    var contactUsTV: TextView? = null
    var termsConditionsTV: TextView? = null
    var status: String? = ""
    var notificationApiCall : NotificationApiCall?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_setting, container, false);
        init(view)
        notificationApiCall = NotificationApiCall(this)

        return view
    }


    private fun onClick() {
        notification_btn!!.setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener { view, isChecked ->
            if (status.equals("1")) {
                status = "0"
                notificationApiCall!!.changeNotificationStatus(status!!, baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!,this)
            } else {
                status = "1"
                notificationApiCall!!.changeNotificationStatus(status!!, baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!,this)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        baseActivity.store!!.setBoolean("edit_back",true)
    }

    private fun init(view: View?) {
        notification_btn = view!!.findViewById(R.id.switch_button) as SwitchButton
        changePasswordTV = view!!.findViewById(R.id.changePasswordTV) as TextView
        rateAppTV = view!!.findViewById(R.id.rateAppTV) as TextView
        shareAppTV = view!!.findViewById(R.id.shareAppTV) as TextView
        aboutUsTV = view!!.findViewById(R.id.aboutUsTV) as TextView
        contactUsTV = view!!.findViewById(R.id.contactUsTV) as TextView
        termsConditionsTV = view!!.findViewById(R.id.termsConditionsTV) as TextView
        changePasswordTV!!.setOnClickListener(this)
        shareAppTV!!.setOnClickListener(this)
        rateAppTV!!.setOnClickListener(this)
        aboutUsTV!!.setOnClickListener(this)
        termsConditionsTV!!.setOnClickListener(this)
        contactUsTV!!.setOnClickListener(this)
        val profileData = baseActivity.store!!.getProfileData()
        setCheck(profileData)
    }

    private fun getProfile() {
        baseActivity.showProgressDialog(baseActivity)
        val call = RetrofitHandler
                .getInstance()
                .api
                .getProfile(baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<ProfileData> {
            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                baseActivity.hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {

                    baseActivity.store!!.setProfileData(response.body() as ProfileData)
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        baseActivity.goToLoginActivity()
                    } else {
                        baseActivity.showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<ProfileData>, t: Throwable) {
                baseActivity.hideProgressDialog()
               // baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }

    private fun setCheck(body: ProfileData?) {
        if(body!!.notification_status==1) {
            status = "1"
            notification_btn!!.isChecked = true
        } else {
            status = "0"
            notification_btn!!.isChecked = false
        }
        onClick()

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.changePasswordTV -> {
                val intent = Intent(activity, CommonActivity::class.java)
                intent.putExtra("change_password","change_password")
                startActivity(intent)
            }
            R.id.shareAppTV -> {
                val appPackageName = baseActivity.packageName
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=$appPackageName")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.rateAppTV -> {
                val appPackageName = baseActivity.packageName
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            R.id.aboutUsTV -> {
                baseActivity.store!!.saveString("about_url","http://pos.rajagsm.in/getStaticPages/aboutUs")
                val intent = Intent(activity, CommonActivity::class.java)
                intent.putExtra("about_us","about_us")
                startActivity(intent)
            }
            R.id.contactUsTV -> {
                val intent = Intent(activity, CommonActivity::class.java)
                intent.putExtra("contact_us","contact_us")
                startActivity(intent)
            }
            R.id.termsConditionsTV -> {
                baseActivity.store!!.saveString("about_url","http://pos.rajagsm.in/getStaticPages/termsAndConditions")
                val intent = Intent(activity, CommonActivity::class.java)
                intent.putExtra("about_us","about_us")
                startActivity(intent)
            }
        }
    }

    override fun onResponseSuccess(data: NotificationStatusData?) {
        baseActivity.showToast(data!!.message,false)
        getProfile()
    }

    override fun onResponseFailure(message: String?) {
        baseActivity.showToast(message!!,false)
    }

    override fun onShowProgress() {
        baseActivity.showProgressDialog(baseActivity)
    }

    override fun onHideProgress() {
        baseActivity.hideProgressDialog()
    }

    override fun onUnauthorize(message: String?) {
         baseActivity.goToLoginActivity()
    }
}