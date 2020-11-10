package com.activedistribution.utils

import android.app.Fragment
import com.activedistribution.retrofitManager.ApiInterface
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.LatLng

object Constants {

    @JvmField
    var UNAUTHORIZED: Int = 401
    @JvmField
    var STATUS_OK: Int = 200
    @JvmField
    var UNVERIFIED: Int = 403
    @JvmField
    var BAD_REQUEST: Int = 400
    @JvmField
    var NOT_ACCEPTABLE: Int = 406
    @JvmStatic
    val USER_ID: String = "user_id"
    val REMEMBER_TOKEN: String = "remember_token"
    @JvmStatic
    val LOGIN: String = "is_login"
    @JvmStatic
    val DEVICE_TOKEN = "device_token"
    @JvmField
    var NOTIFICATION_STATUS:String = "0"
    @JvmField
    var IMAGE_BASE_URL:String = "http://activdistribution.com/"
    @JvmField
    val PLAY_SERVICES_RESOLUTION_REQUEST = 1234
    @JvmField
    val DISPLAY_MESSAGE_ACTION = "com.activedistribution.DISPLAY_MESSAGE"
    @JvmField
    val FORGROUND = "forground_notification"
    @JvmField
     var BROADCAST_ACTION ="com.activedistribution.android.threadsample.BROADCAST"
    @JvmField
    var EXTENDED_DATA_STATUS = "com.activedistribution.android.threadsample.STATUS"
    @JvmField
    val LOCATION_INTERVAL = 5000
    @JvmField
    val FASTEST_LOCATION_INTERVAL = 5000


}
