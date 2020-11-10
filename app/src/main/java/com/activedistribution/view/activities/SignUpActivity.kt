package com.activedistribution.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.activedistribution.R
import com.activedistribution.model.SignUpData
import com.activedistribution.presenter.signup.SignUpPresenter
import com.activedistribution.view.customfont.Button
import com.activedistribution.view.customfont.EditText
import com.activedistribution.presenter.signup.SignUpInterface
import com.activedistribution.utils.Constants

class SignUpActivity : BaseActivity(), SignUpInterface {

    private var emailET: EditText? = null
    private var passwordET: EditText? = null
    private var confirmPasswordET: EditText? = null
    private var iv_back: ImageView? = null
    private var layoutLL: LinearLayout? = null
    var signUpPresenter: SignUpPresenter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signUpPresenter = SignUpPresenter(this)
        initUI()
    }

    private fun initUI() {
        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)
        confirmPasswordET = findViewById(R.id.confirmPasswordET)
        iv_back = findViewById(R.id.iv_back)
        val signUpBT = findViewById<Button>(R.id.signUpBT)
         layoutLL = findViewById(R.id.layoutLL)
        signUpBT.setOnClickListener {
            val email = emailET!!.text!!.toString()
            val password = passwordET!!.text!!.toString()
            val confirmPassword = confirmPasswordET!!.text!!.toString()
            signUpPresenter!!.checkValidation(email, password, confirmPassword,store!!.getString(Constants.DEVICE_TOKEN)!!,"A")
        }
        iv_back!!.setOnClickListener {
            val intent = Intent(this,ChoosenActivity::class.java)
            startActivity(intent)
        }
        layoutLL!!.setOnTouchListener(View.OnTouchListener { view, ev ->
            hideSoftKeyboard(view)
            false
        })
        emailET!!.setFilters(arrayOf(noSpaceFilter))
        passwordET!!.setFilters(arrayOf(noSpaceFilter))
        confirmPasswordET!!.setFilters(arrayOf(noSpaceFilter))
    }

    override fun onSuccess(signUpResponse: SignUpData) {
        store!!.setInt(Constants.USER_ID,signUpResponse.user_id)
        store!!.saveString(Constants.NOTIFICATION_STATUS,signUpResponse.user_info.notification_status)
        store!!.saveString(Constants.REMEMBER_TOKEN,signUpResponse.user_info.remember_token)
        startActivity(Intent(this, CreateProfileActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
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
        val intent = Intent(this,ChoosenActivity::class.java)
        startActivity(intent)
    }
}
