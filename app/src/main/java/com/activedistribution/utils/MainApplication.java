package com.activedistribution.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
/**
 * Created by Dell on 8/23/2017.
 */

public class MainApplication extends MultiDexApplication {

    private static MainApplication mainApplication;

    public AppService gpsService;
    GpsLocationReceiver gpsLocationReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
        gpsLocationReceiver=new GpsLocationReceiver();
       // FirebaseApp.initializeApp(this);
        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);*/


        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_REPLACED");
        registerReceiver(gpsLocationReceiver, filter);



    }
    public void startServiceMethod(){
        final Intent i = new Intent(this, AppService.class);
        startService(i);
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopServiceMethod(){
        try {
            if (serviceConnection != null) {
                final Intent i = new Intent(this, AppService.class);
//            bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
                stopService(i);
                unbindService(serviceConnection);
            }
        }catch (Exception ex){

        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("AppService")) {
                gpsService = ((AppService.LocationServiceBinder) service).getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("AppService")) {
                gpsService = null;
            }
        }
    };



    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    public static synchronized MainApplication getInstance() {
        return mainApplication;
    }
}