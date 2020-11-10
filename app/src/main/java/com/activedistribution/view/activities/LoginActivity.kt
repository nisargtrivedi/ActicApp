package com.activedistribution.view.activities

import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.activedistribution.R
import com.activedistribution.model.LoginData
import com.activedistribution.model.ProfileData
import com.activedistribution.presenter.login.LoginPresenter
import com.activedistribution.view.customfont.Button
import com.activedistribution.view.customfont.EditText
import com.activedistribution.presenter.login.LoginInterface
import com.activedistribution.utils.Constants
import com.activedistribution.utils.MainApplication
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class LoginActivity : BaseActivity(), View.OnClickListener, LoginInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    var emailET: EditText? = null
     var passwordET: EditText? = null
     var loginBT: Button? = null
     var forgotPasswordTV: TextView? = null
     var iv_back: ImageView? = null
     var layoutLL: LinearLayout? = null
     var loginPresenter: LoginPresenter? = null
     private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
     private var mGoogleApiClient: GoogleApiClient? = null
     private var mLocationRequest: LocationRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginPresenter = LoginPresenter(this)
        checkLocationPermission()
        initUI()
    }

    private fun initUI() {
        emailET = findViewById(R.id.emailET) as EditText
        passwordET = findViewById(R.id.passwordET) as EditText
        loginBT = findViewById(R.id.loginBT) as Button
        forgotPasswordTV = findViewById(R.id.forgotPasswordTV) as TextView
        layoutLL = findViewById(R.id.layoutLL) as LinearLayout
        iv_back = findViewById(R.id.iv_back) as ImageView
        loginBT!!.setOnClickListener(this)
        forgotPasswordTV!!.setOnClickListener(this)
        iv_back!!.setOnClickListener(this)
        emailET!!.setFilters(arrayOf(noSpaceFilter))
        passwordET!!.setFilters(arrayOf(noSpaceFilter))
        layoutLL!!.setOnTouchListener(View.OnTouchListener { view, ev ->
            hideSoftKeyboard(view)
            false
        })

        if(checkLocationPermission()) {
            getServiceLocation()
        }
    }

    fun getServiceLocation() {
        try {

            if (mGoogleApiClient == null) {
                try {
                    mGoogleApiClient = GoogleApiClient.Builder(getApplicationContext())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build()
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
            if (!mGoogleApiClient!!.isConnected()) {
                mGoogleApiClient!!.connect()
            }
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        } catch (e :Exception) {
            // TODO: handle exception.
            e.printStackTrace();
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.loginBT -> {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    val email = emailET!!.text!!.toString().trim { it <= ' ' }
                    val password = passwordET!!.text!!.toString().trim { it <= ' ' }
                    if (store!!.getString("currentLatitude") == null || store!!.getString("currentLongitude") == null) {
                        store!!.saveString("currentLatitude", "0.00")
                        store!!.saveString("currentLongitude", "0.00")
                        loginPresenter!!.checkValidation(email, password, store!!.getString(Constants.DEVICE_TOKEN)!!, "A", store!!.getString("currentLatitude")!!.toDouble(), store!!.getString("currentLongitude")!!.toDouble())
                    } else {
                        loginPresenter!!.checkValidation(email, password, store!!.getString(Constants.DEVICE_TOKEN)!!, "A", store!!.getString("currentLatitude")!!.toDouble(), store!!.getString("currentLongitude")!!.toDouble())
                    }
                } else
                    Toast.makeText(this,"Please enable location services to start a job", Toast.LENGTH_LONG).show()
            }
            R.id.forgotPasswordTV -> {
                val intent = Intent(this,ForgotPasswordActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            R.id.iv_back -> {
                val intent = Intent(this,ChoosenActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onError(error: String) {
        showToast(error, false)
    }

    override fun onSuccess(message: LoginData) {
        store!!.setInt(Constants.USER_ID,message.user_info.id)
        store!!.setBoolean(Constants.LOGIN,true)
        store!!.saveString(Constants.NOTIFICATION_STATUS, message.user_info.notification_status.toString())
        store!!.saveString(Constants.REMEMBER_TOKEN,message.user_info.remember_token)
        store!!.setProfileData(message.user_info as ProfileData)
        showToast("You have been successfully logged in", false)
        if(message!!.user_info.firstname!=null) {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)

        } else {
            startActivity(Intent(this, CreateProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
        MainApplication.getInstance().startServiceMethod()
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

    override fun onConnected(p0: Bundle?) {
        if(checkLocationPermission()) {
            val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

            } else {
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                store!!.saveString("currentLatitude",currentLatitude.toString())
                store!!.saveString("currentLongitude",currentLongitude.toString())
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode())
        }
    }

    override fun onLocationChanged(location: Location?) {
        currentLatitude = location!!.getLatitude()
        currentLongitude = location!!.getLongitude()
        store!!.saveString("currentLatitude",currentLatitude.toString())
        store!!.saveString("currentLongitude",currentLongitude.toString())
    }

}
