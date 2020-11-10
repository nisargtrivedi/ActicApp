package com.activedistribution.utils

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetManager
import android.provider.Settings
import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

import java.io.IOException
import java.io.InputStream

object Utils {

    private val TAG = "Utils"


    private fun loadJSONFromAsset(context: Context, jsonFileName: String): String? {
        var json: String? = null
        var `is`: InputStream? = null
        try {
            val manager = context.assets
            Log.d(TAG, "path $jsonFileName")
            `is` = manager.open(jsonFileName)
            val size = `is`!!.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, "UTF-8")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    private fun String(buffer: ByteArray, s: String): String? {
           return null
    }

    @JvmStatic
    fun onResoinseError(response: Response<*>): String {
        var message = ""
        var errorBody = ""
        if (response.code() == 500) {
            message = "Something went wrong!"
        } else {
            try {
                errorBody = response.errorBody()!!.string()
                val msgobj = JSONObject(errorBody)
                message = msgobj.optString("message")
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return message
      /*  var message = ""
        var errorBody = ""
        val msgobj = ""

        try {
            errorBody = response.errorBody()!!.string()
            val msgobj = JSONObject(errorBody)
           // message = response.message()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return msgobj*/
    }

    fun getRequestBodyParam(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/form-data"), value)
    }

    @JvmStatic
    fun buildAlertMessageNoGps(mContext: Context) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id -> mContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()

    }

    @JvmStatic
    fun isMyServiceRunning(c: Context, serviceClass: Class<*>): Boolean {
        val manager = c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}