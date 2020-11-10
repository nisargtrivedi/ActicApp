package com.activedistribution.view.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.activedistribution.R
import com.activedistribution.presenter.forgotPassword.forgotPasswordInterface
import com.activedistribution.presenter.forgotPassword.forgotPasswordPresenter
import com.activedistribution.view.customfont.Button
import com.activedistribution.view.customfont.EditText
import android.view.WindowManager
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.activedistribution.model.NotificationStatusData


class ForgotPasswordActivity : BaseActivity(), forgotPasswordInterface {

    private var emailET: EditText? = null
    private var resetPasswordBT: Button? = null
    private var iv_back: ImageView? = null
    private var layoutLL: LinearLayout? = null
    var forgotPasswordPresenter: forgotPasswordPresenter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        forgotPasswordPresenter = forgotPasswordPresenter(this)
        initUI()
    }

    private fun initUI() {
        emailET = findViewById(R.id.emailET) as EditText
        iv_back = findViewById(R.id.iv_back) as ImageView
        layoutLL = findViewById(R.id.layoutLL) as LinearLayout
        resetPasswordBT = findViewById(R.id.resetPasswordBT)
        emailET!!.setFilters(arrayOf(noSpaceFilter))
        resetPasswordBT!!.setOnClickListener {
            val email = emailET!!.text!!.toString().trim { it <= ' ' }
            forgotPasswordPresenter!!.checkValidation(email)
        }
        iv_back!!.setOnClickListener {
            finish()
        }
        layoutLL!!.setOnTouchListener(View.OnTouchListener { view, ev ->
            hideSoftKeyboard(view)
            false
        })
    }

    override fun onSuccess(message: NotificationStatusData) {
        showDialog()
    }

    private fun showDialog() {
        var cancelIV: ImageView?
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCanceledOnTouchOutside(false)
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val dialogWindow = dialog.getWindow()
        cancelIV = dialog.findViewById(R.id.cancelIV) as ImageView
        cancelIV!!.setOnClickListener { val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)}
        dialogWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialogWindow.setDimAmount(0.80f)
        dialog.show()
    }

    override fun onError(error: String) {
        showToast(error, false)
    }

    override fun onShowProgress() {
        showProgressDialog(this)
    }

    override fun onHideProgress() {
        hideProgressDialog()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
