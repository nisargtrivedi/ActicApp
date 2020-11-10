package com.activedistribution.view.activities

import android.Manifest
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.activedistribution.R
import com.activedistribution.model.NotificationStatusData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.*
import com.activedistribution.view.fragments.BottomNavigationFragment
import com.activedistribution.view.fragments.EditProfileFragment
import com.activedistribution.view.fragments.HomeFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MainActivity: BaseActivity(),GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    internal var doubleBackToExitPressedOnce = false
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var display: Boolean? = null


    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                display = true
                setNotificationData(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        print("START PART CALLED IN MAIN ACTIVITY")
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1000)
        }else{
            print("ELSE PART CALLED IN MAIN ACTIVITY")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        checkLocationPermission()
        val intent = Intent(applicationContext,AppService::class.java)
        intent.putExtra("token", store!!.getString(Constants.REMEMBER_TOKEN)!!)
        startService(intent)




        if (savedInstanceState == null) {
            loadFragment1(BottomNavigationFragment(), R.id.frame_layout)
        }
        if (getIntent().getBooleanExtra("is_push", false)) run {
            if (getIntent() != null) {
                display = false
                setNotificationData(getIntent())
            }
        }
    }
//    protected ServiceConnection mServerConn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder binder) {
//            Log.d(LOG_TAG, "onServiceConnected");
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.d(LOG_TAG, "onServiceDisconnected");
//        }
//    }


    private fun setNotificationData(intent: Intent) {
        if (intent.extras!!.getBoolean("is_push", false)) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()
            if(display!!) {
                val fragment = HomeFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.job_frame, fragment).addToBackStack(null)
                        .commit()
            } else {
                store!!.setBoolean("is_push", intent.extras!!.getBoolean("is_push"))
                store!!.saveString("job_id", intent.extras.getString("job_id"))
                val intent = Intent(this, CommonActivity::class.java)
                intent.putExtra("is_push", "is_push")
                startActivity(intent)
            }
        }
    }


    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
         if (fragment is BottomNavigationFragment) {
            if (doubleBackToExitPressedOnce) {
                finish()
                finishAffinity()
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data!=null) {
            var fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as EditProfileFragment
            if(!store!!.getBoolean("places")) {
                fragment.setImage(data, requestCode)
            } else {
                try {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    fragment.setPlace((place.address as String?)!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

   override fun onPause() {
        super.onPause()
       LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    fun changeLocation(latitude: Double, longitude: Double,jobID:String) {
        val call = RetrofitHandler
                .getInstance()
                .api
                .changeLocation(latitude.toString(),longitude.toString(),store!!.getString(Constants.REMEMBER_TOKEN)!!,jobID,"0")

        call.enqueue(object : Callback<NotificationStatusData> {
            override fun onResponse(call: Call<NotificationStatusData>, response: Response<NotificationStatusData>) {
                hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {
                    showToast(response.body()?.speed_notify!!, false)
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        goToLoginActivity()
                    } else {
                        showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<NotificationStatusData>, t: Throwable) {
                hideProgressDialog()
                showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(Constants.DISPLAY_MESSAGE_ACTION))
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
        changeLocation(location.latitude,location.longitude,"0")
    }

    var curTime = 0.0
    var oldLat = 0.0
    var oldLon = 0.0

}


