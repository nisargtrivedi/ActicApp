package com.activedistribution.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings

class LReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        println("CALLED RECEIVER-------->")
//        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
//            try {
//                val locationManager = p0!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    p0!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//                    try {
//                        println("UPDATE PACKAGE CALLED----->")
//                    } catch (ex: Exception) {
//                        println("Upgrade Not Done")
//                    }
//                }
//            } catch (ex: Exception) {
//            }
//        }
    }
}