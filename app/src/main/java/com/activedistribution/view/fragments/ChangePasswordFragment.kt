package com.activedistribution.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.activedistribution.R
import com.activedistribution.model.NotificationStatusData
import com.activedistribution.presenter.changePassword.ChangePasswordInterface
import com.activedistribution.presenter.changePassword.changePasswordPresenter
import com.activedistribution.utils.Constants
import com.activedistribution.view.activities.MainActivity

class ChangePasswordFragment : BaseFragment(), ChangePasswordInterface {


    private var iv_back: ImageView? = null
    private var oldpasswordET: EditText? = null
    private var newpasswordET: EditText? = null
    private var confirmpasswordET: EditText? = null
    private var submitBT: Button? = null
    private var layoutLL: LinearLayout? = null
    var presenter: changePasswordPresenter?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_change_password, container, false)
        presenter = changePasswordPresenter(this)
        initUI(v)
        return v
    }

    private fun initUI(v: View) {
        iv_back = v.findViewById(R.id.iv_back)
        oldpasswordET = v.findViewById(R.id.oldpasswordET)
        newpasswordET = v.findViewById(R.id.newpasswordET)
        confirmpasswordET = v.findViewById(R.id.conPasswordET)
        oldpasswordET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        newpasswordET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        confirmpasswordET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        layoutLL = v.findViewById(R.id.layoutLL)
        oldpasswordET!!.setText(oldpasswordET!!.getText().toString().replace(" ",""))
        newpasswordET!!.setText(newpasswordET!!.getText().toString().replace(" ",""))
        confirmpasswordET!!.setText(confirmpasswordET!!.getText().toString().replace(" ",""))
        submitBT = v.findViewById(R.id.submitBT)
        submitBT!!.setOnClickListener {
            val oldPassword = oldpasswordET!!.text.toString().trim { it <= ' ' }
            val newPassword = newpasswordET!!.text.toString().trim { it <= ' ' }
            val confirmPassword = confirmpasswordET!!.text.toString().trim { it <= ' ' }
            presenter!!.checkValidation(oldPassword, newPassword, confirmPassword,baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)
        }
        iv_back!!.setOnClickListener {
            activity!!.onBackPressed()
        }
        layoutLL!!.setOnTouchListener(View.OnTouchListener { view, ev ->
            baseActivity.hideSoftKeyboard(view)
            false
        })
    }

    override fun onSuccess(data: NotificationStatusData) {
        baseActivity.store!!.setBoolean("edit_back",false)
        startActivity(Intent(baseActivity, MainActivity::class.java))
    }

    override fun onError(error: String) {
        baseActivity.showToast(error, false)
    }

    override fun onUnauthorised(error: String) {
        baseActivity.goToLoginActivity()
    }

    override fun onShowProgress() {
        baseActivity.showProgressDialog(baseActivity)
    }

    override fun onHideProgress() {
        baseActivity.hideProgressDialog()
    }
}
