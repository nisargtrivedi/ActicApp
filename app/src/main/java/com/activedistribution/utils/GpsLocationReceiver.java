package com.activedistribution.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

public class GpsLocationReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("CALLED RECEIVER-------->");
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        //Toast.makeText(context,"Please enable location services to start a job",Toast.LENGTH_LONG).show();
                    try {
                        System.out.println("UPDATE PACKAGE CALLED----->");


                    }catch (Exception ex){
                        System.out.println("Upgrade Not Done");
                    }


                }
            }catch (Exception ex){

            }
        }
    }
}
//Toast.makeText(context, "Location Services is Off. Please turn on location service.",
//        Toast.LENGTH_SHORT).show();
