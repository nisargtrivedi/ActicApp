package com.activedistribution.view.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import com.activedistribution.R

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
        initUI()
    }

    private fun initUI() {
        Handler().postDelayed({
            initFCM()
          }, SPLASH_TIME_OUT.toLong())
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        showToast("Got Permission",false)
                    } else {
                        showToast("Permission denied",false)
                    }
                    return
                }
            }
        }
    }

    companion object {
        private val SPLASH_TIME_OUT = 2000
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

}
