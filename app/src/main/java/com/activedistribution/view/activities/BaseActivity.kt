package com.activedistribution.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.activedistribution.R
import com.activedistribution.model.JobsListData
import com.activedistribution.model.NotificationStatusData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.*
import com.activedistribution.view.fragments.EditProfileFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {

    private var toast: Toast? = null
    var store: PrefStore?=null
    private var permCallback: PermCallback? = null
    private var reqCode: Int = 0
    internal var dialog: Dialog? = null
    internal var wait_tv: TextView?=null
    internal var countDownTimer: CountDownTimer? = null
    var currentLatitude: Double = 0.toDouble()
    var currentLongitude: Double = 0.toDouble()

    internal var filter : IntentFilter? = IntentFilter()
    internal var gpsLocationReceiver: GpsLocationReceiver? = GpsLocationReceiver()

    lateinit var manager: LocationManager
    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = PrefStore(this)

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

         manager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!

        filter = IntentFilter()
        filter!!.addAction("android.location.PROVIDERS_CHANGED")
        gpsLocationReceiver=GpsLocationReceiver()
        registerReceiver(gpsLocationReceiver, filter)

    }

    private fun setWindowFlag(activity: BaseActivity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }



    fun showToast(msg: String, isError: Boolean) {
        val toastTV: TextView?
        val parentLL: LinearLayout?
        val view = View.inflate(this, R.layout.layout_toast, null)
         toastTV = view.findViewById(R.id.toastTV) as TextView
        if (isError) {
             parentLL = view.findViewById(R.id.parentLL) as LinearLayout
             parentLL.setBackgroundColor(ContextCompat.getColor(this, R.color.Black))
        }
        toastTV.text = msg
        toast!!.view = view
        toast!!.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast!!.show()
    }


    fun checkPermissions(perms: Array<String>, requestCode: Int, permCallback: PermCallback): Boolean {
        this.permCallback = permCallback
        this.reqCode = requestCode
        val permsArray = ArrayList<String>()
        var hasPerms = true
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                permsArray.add(perm)
                hasPerms = false
            }
        }
        return if (!hasPerms) {
            val permsString = arrayOfNulls<String>(permsArray.size)
            for (i in permsArray.indices) {
                permsString[i] = permsArray[i]
            }
            ActivityCompat.requestPermissions(this@BaseActivity, permsString, 99)
            false
        } else
            true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var permGrantedBool = false
        when (requestCode) {
            99 -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        showToast(getString(R.string.not_sufficient_permissions, getString(R.string.app_name)), true)
                        permGrantedBool = false
                        break
                    } else {
                        permGrantedBool = true
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool) {
                        permCallback!!.permGranted(reqCode)
                    } else {
                        permCallback!!.permDenied(reqCode)
                    }
                }
            }
        }
    }



    interface PermCallback {
        fun permGranted(resultCode: Int)

        fun permDenied(resultCode: Int)
    }

    fun loadFragment1(fragment: Fragment, id: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        ft.replace(id, fragment)
        ft.commit()
    }

    fun loadFragment(fragment: Fragment, id: Int) {
        val ft = supportFragmentManager.beginTransaction().addToBackStack(null)
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        ft.replace(id, fragment)
        ft.commit()
    }

    fun logout() {

        MainApplication.getInstance().stopServiceMethod()
//        val intent2 = Intent(applicationContext,AppService::class.java)
//        stopService(intent2)

        val call = RetrofitHandler
                .getInstance()
                .api
                .logout(store!!.getString(Constants.REMEMBER_TOKEN)!!)
        call.enqueue(object : Callback<NotificationStatusData> {
            override fun onResponse(call: Call<NotificationStatusData>, response: Response<NotificationStatusData>) {
                if (response.code() == Constants.STATUS_OK) {
                    store!!.setBoolean(Constants.LOGIN,false)
                    goToLoginActivity()
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        goToLoginActivity()
                    } else {
                        showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<NotificationStatusData>, t: Throwable) {
                showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })

    }

    fun initFCM() {
        if (checkPlayServices()) {
           var refreshedToken = store!!.getString(Constants.DEVICE_TOKEN)
            if (refreshedToken != null) {
                if (!store!!.getBoolean(Constants.LOGIN)) {
                    val intent = Intent(this, ChoosenActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                } else {
                    val profileData = store!!.getProfileData()
                    if(profileData.firstname!=null) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    } else {
                        val intent = Intent(this, CreateProfileActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    }
                }
          } else {
                if (!store!!.getBoolean(Constants.LOGIN)) {
                    val intent = Intent(this, ChoosenActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                } else {
                    val profileData = store!!.getProfileData()
                    if(profileData.firstname!=null) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    } else {
                        val intent = Intent(this, CreateProfileActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    }
                }
            }
        }
    }



    fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            print("IF CALLED--->")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                            (Manifest.permission.ACCESS_COARSE_LOCATION)) && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        SplashActivity.MY_PERMISSIONS_REQUEST_LOCATION)
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        SplashActivity.MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            print("ELSE CALLED--->")
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)

                return true
        }
    }

    override fun onResume() {
        print("text------------>")
        store!!.setBoolean(Constants.FORGROUND, true)

        super.onResume()
        }

    override fun onPause() {
        super.onPause()
        store!!.setBoolean(Constants.FORGROUND, false)
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show()
            } else {
                showToast(getString(R.string.this_device_not_supported),false)
            }
            return false
        }
        return true
    }

     fun goToLoginActivity() {
        store!!.setBoolean(Constants.LOGIN,false)
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
//         stopService(intent)

         overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun goToMapActivity(lat:String,lng:String,jobID:String,token:String,userID:String,data:ArrayList<JobsListData.Locations>) {
        val intent = Intent(this,MapsActivity::class.java)
        intent.putExtra("Latitude",lat)
        intent.putExtra("Longitude",lng)
        intent.putExtra("JobID",jobID)
        intent.putExtra("token",token)
        intent.putExtra("userID",userID)
        intent.putExtra("data",data)
        println("JOB ID IN FUNCTION =="+jobID)
        startActivity(intent)
        //stopService(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun changeDateFormat(dateString: String, sourceDateFormat: String, targetDateFormat: String): String {
        val df = SimpleDateFormat(sourceDateFormat, Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = df.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        df.timeZone = TimeZone.getDefault()
        val outputDateFormat = SimpleDateFormat(targetDateFormat, Locale.getDefault())
        return outputDateFormat.format(date)
    }

     fun logout_dialog() {
        AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, which -> logout()
                })
                .setNegativeButton("No", null)
                .show()
    }

    fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showProgressDialog(context: Context) {
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_progress)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val dialogWindow = dialog!!.getWindow()
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogWindow!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialogWindow!!.setDimAmount(0.70f)
        dialog!!.setCancelable(false)
        wait_tv = dialog!!.findViewById(R.id.wait_tv)
        val secs = 5
        countDownTimer = object : CountDownTimer(((secs + 1) * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                if (seconds == 5L) {
                    wait_tv!!.setText("Please wait.")
                } else if (seconds == 4L) {
                    wait_tv!!.setText("Please wait..")
                } else if (seconds == 3L) {
                    wait_tv!!.setText("Please wait...")
                } else if (seconds == 2L) {
                    wait_tv!!.setText("Please wait....")
                } else if (seconds == 1L) {
                    wait_tv!!.setText("Please wait.....")
                } else {
                    wait_tv!!.setText("Please wait......")
                }
            }

            override fun onFinish() {
                countDownTimer!!.start()
            }
        }
        countDownTimer!!.start()

        dialog!!.show()
    }

    fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing()) {
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            } else {
                Log.d("onTick", "onTick: " + "------Null")
            }
            dialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var fragment = supportFragmentManager.findFragmentById(R.id.common_frame) as EditProfileFragment
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

    var noSpaceFilter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        var filtered = ""
        for (i in start until end) {
            val character = source[i]
            if (!Character.isWhitespace(character)) {
                filtered += character
            }
        }
        filtered
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(gpsLocationReceiver)
    }

    fun showImageDialog(resumeURL:String) {
// Create the fragment and show it as a dialog.
val newFragment = ImagePreviewDialogFragment.newInstance(resumeURL);
//newFragment.setTargetFragment(Age_RecyclerAdapter.this, 300);
newFragment.show(getSupportFragmentManager(), "");
}

    // false means ""

}




