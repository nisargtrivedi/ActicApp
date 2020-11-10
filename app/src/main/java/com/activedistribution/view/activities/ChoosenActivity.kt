package com.activedistribution.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.activedistribution.R

class ChoosenActivity : BaseActivity() {

     var loginLL: LinearLayout?=null
     var signUpLL: LinearLayout?=null
     var termsPrivacyTV: TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        initUI()
    }

    @Suppress("DEPRECATION")
    private fun initUI() {
        loginLL = findViewById(R.id.loginLL)
        signUpLL = findViewById(R.id.signUpLL)
        termsPrivacyTV = findViewById(R.id.termsPrivacyTV)
        termsPrivacyTV!!.setClickable(true)
        termsPrivacyTV!!.setMovementMethod(LinkMovementMethod.getInstance())
        loginLL!!.setOnClickListener(clickListener)
        signUpLL!!.setOnClickListener(clickListener)
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.loginLL-> {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            R.id.signUpLL-> {
                val intent = Intent(this,SignUpActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }
}
