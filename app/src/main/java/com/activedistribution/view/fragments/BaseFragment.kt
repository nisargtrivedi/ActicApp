package com.activedistribution.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.activedistribution.view.activities.BaseActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsRequest.*
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

open class BaseFragment : Fragment() {
    protected lateinit var baseActivity: BaseActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }
    override fun onResume() {
        super.onResume()
        print("LOCAION SERVICES===>"+isLocationEnabled())

        //val builder = Builder()

// ...

//        val client: SettingsClient = LocationServices.getSettingsClient(context!!)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//        if (!isLocationEnabled()) {
//            context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//        }else{
//            print("CALLED ELSE IN BASE FRAGEMENT--------------")
//        }

//            val permission = ContextCompat.checkSelfPermission(baseActivity,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//
//        val permission2 = ContextCompat.checkSelfPermission(baseActivity,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(baseActivity,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                    1000)
//        }
//        else if (permission2 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(baseActivity,
//                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
//                    1001)
//        }else{
//            print("ELSE PART CALLED IN MAIN ACTIVITY")
//            //context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//        }
    }
}


