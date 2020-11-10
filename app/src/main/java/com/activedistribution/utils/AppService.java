package com.activedistribution.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.activedistribution.model.NotificationStatusData;
import com.activedistribution.retrofitManager.RetrofitHandler;
import com.activedistribution.view.activities.MainActivity;
import com.activedistribution.view.activities.MapsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = AppService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    GoogleApiClient mGoogleApiClient;
    ThreadDemo td;
    private String token;
    private Context mContext;

    public class LocationServiceBinder extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(12345678, getNotification());
        }else{
            startForeground(12345678,getNotification());
        }
        token = intent.getStringExtra("token");

        td = new ThreadDemo(60000);
        td.start();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        mLocationClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.d(TAG, "Connected to Google API");
            mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
            mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);
            int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
            //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes
            mLocationRequest.setPriority(priority);
            mLocationClient.connect();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {

            //final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

       // getServiceLocation();
      //  LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        System.out.println("CALLED TIME Method ============>");
            if (location != null) {
            Log.d(TAG, "== location != null");
            int speed=(int) ((location.getSpeed()*3600)/1000);
            int mspeed = (int) (speed / 1.609344);
            double s = ((location.getSpeed()*3600)/1000)/1.609344;
            if(mspeed>0){
                if(mspeed>5){
                    Toast.makeText(this,"you are going to fast",Toast.LENGTH_LONG).show();
                }
            }
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),s+"");
        }

    }


    double oldLat=0;
    double oldLng=0;
    double curTime= 0;
    private void sendMessageToUI(String lat, String lng,String speed) {
        changeLocation(lat,lng,speed);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }

   /* @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }*/

    private class ThreadDemo extends Thread {
        long time = 0l;

        public ThreadDemo(long l) {
            this.time = l;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(time);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.getMessage();
                    return;
                } catch (Exception e) {
                    e.getMessage();
                    return;
                }
            }
        }
    }
    private Notification getNotification() {
        Notification.Builder builder=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "sv_channel1",
                    "sv_channel1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setSound(null,null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder= new Notification.Builder(getApplicationContext(), "sv_channel1");
        }else{
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            builder=new Notification.Builder(getApplicationContext());
        }


        return builder.build();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getServiceLocation();
            Log.e("Driver SERVICE", "<><><><>><><><<>>");
        }
    };

    public void getServiceLocation() {
        try {
            if (mGoogleApiClient == null) {
                try {
                    mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isGooglePlayServicesAvailable()) {
            createLocationRequest();
        } else {
            Log.e(TAG, "Not Available ...............................");
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability status = GoogleApiAvailability.getInstance();
        int available = status.isGooglePlayServicesAvailable(getApplicationContext());
        if (available != ConnectionResult.SUCCESS) {
            if(status.isUserResolvableError(available)) {
                status.getErrorDialog((Activity) getApplicationContext(), available, 2404).show();
            } else {
            status.getErrorDialog((Activity) getApplicationContext(), available,0).show();
        }
            return false;
        }
        return true;
    }

    public void createLocationRequest() {
        Log.e("Creating ", "...Location Request");
        try {
            if (mLocationRequest == null) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
                mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);
                mLocationRequest.setSmallestDisplacement(100);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                builder.setAlwaysShow(true);

                getServiceLocation();
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.i(TAG, "All location settings are satisfied.");
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the result
                                    // in onActivityResult().
                                    //status.startResolutionForResult(this, 100);
                                } catch (Exception e) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                                break;
                        }
                    }
                });

            } else {
                Log.e("Creating ", "...mLocationRequest NOT NULL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeLocation(String lat, String lng,String speed) {
        Log.e("changeLocation", "changeLocation>>>>>>>>>>  true ");
       new RetrofitHandler().getInstance()
                .getApi().changeLocation(lat,lng,token,"0",speed).enqueue(new Callback<NotificationStatusData>() {
                    @Override
                    public void onResponse(Call<NotificationStatusData> call, Response<NotificationStatusData> response) {
                        if(response.code()==Constants.UNAUTHORIZED) {

                        }
                        if(response.body()!=null) {
                            if (!response.body().speed_notify.isEmpty()) {
                                Toast.makeText(AppService.this, response.body().speed_notify, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationStatusData> call, Throwable t) {

                    }
                });
    }



    private static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        long newTime= System.currentTimeMillis();
        long timeDifferent = newTime - distanceInMeters;
        long speed = distanceInMeters/timeDifferent;
        return speed;
    }


    public double calculationBydistance(double lat1, double lon1, double lat2, double lon2){
        double radius = 6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }

}
