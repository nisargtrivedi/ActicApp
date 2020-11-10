package com.activedistribution.view.fragments

import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.activedistribution.R
import com.activedistribution.model.AcceptRejectData
import com.activedistribution.model.JobDetail
import com.activedistribution.model.JobsListData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.Utils
import com.activedistribution.view.activities.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("ValidFragment")
 class JobDetailFragment() : BaseFragment() {

     var accptRejectLL: LinearLayout? = null
     var complte_pauseLL: LinearLayout? = null
     var acceptLL: LinearLayout? = null
     var rejectLL: LinearLayout? = null
     var startLL: LinearLayout? = null
     var completedLL: LinearLayout? = null
     var pauseLL: LinearLayout? = null
     var iv_back: ImageView ?=  null
     var jobProgressTV: TextView ?=  null
     var jobIV: ImageView ?=  null
     var job_id_tv: TextView ?=  null
     var job_date_tv: TextView ?=  null
     var quantity_flyers: TextView ?=  null
     var pauseTV: TextView ?=  null
     private var job_id: String?=null
     var token:String?=null
     var lineOptions: PolylineOptions? = null
     var latLngList: ArrayList<JobsListData.Locations> = ArrayList()
     var latLngList1: ArrayList<AcceptRejectData.Locations> = ArrayList()
     var latLngList2: ArrayList<JobDetail.Locations> = ArrayList()
     var map:GoogleMap?=null
    var imgZoom:ImageView?=null
    var obj:JobsListData.Data?=null
    var job_name:TextView?=null
    var mapFragment:SupportMapFragment?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_jobdetail, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        accptRejectLL = view.findViewById(R.id.acceptReject_LL) as LinearLayout
        acceptLL = view.findViewById(R.id.acceptLL)
        rejectLL = view.findViewById(R.id.rejectLL)
        startLL = view.findViewById(R.id.startLL)
        complte_pauseLL = view.findViewById(R.id.complte_pauseLL) as LinearLayout
        completedLL = view.findViewById(R.id.completedLL)
        pauseLL = view.findViewById(R.id.pauseLL)
        jobProgressTV = view.findViewById(R.id.jobProgressTV)
        iv_back = view.findViewById(R.id.iv_back)
        jobIV = view.findViewById(R.id.jobIV)
        job_id_tv = view.findViewById(R.id.job_id_tv)
        pauseTV = view.findViewById(R.id.pauseTV)
        job_date_tv = view.findViewById(R.id.job_date_tv)
        quantity_flyers = view.findViewById(R.id.quantity_flyers)
        job_name=view.findViewById(R.id.job_name)
         mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        imgZoom=view.findViewById(R.id.imgZoom) as ImageView

        if(baseActivity.store!!.getBoolean("is_push")) {
            getJobDetailApi(baseActivity.store!!.getString("job_id"))
        } else {
            val gson = Gson()
            val json = baseActivity.store!!.getString("jobsListData", "")
             obj = gson.fromJson<JobsListData.Data>(json, JobsListData.Data::class.java)
             setNewData(obj!!)
        }

        iv_back!!.setOnClickListener {
            activity!!.onBackPressed()
        }
        acceptLL!!.setOnClickListener {
            val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                acceptRejectJob(job_id!!, "A")
            }
            else
                Toast.makeText(baseActivity,"Please start location services to start the job",Toast.LENGTH_LONG).show()

        }
        rejectLL!!.setOnClickListener {
            AlertDialog.Builder(baseActivity)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to Reject this job?")
                    .setPositiveButton("Yes", DialogInterface.OnClickListener {
                        dialog, which ->  acceptRejectJob(job_id!!,"R")
                    })
                    .setNegativeButton("No", null)
                    .show()

        }

        startLL!!.setOnClickListener {

            val locationManager = baseActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                acceptRejectJob(job_id!!, "I")
            }
            else{
                Toast.makeText(baseActivity,"Please enable location services to start a job",Toast.LENGTH_LONG).show()
            }
        }

        completedLL!!.setOnClickListener {
            val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                acceptRejectJob(job_id!!, "C")
            }
            else
                Toast.makeText(baseActivity,"Please enable location services to start a job",Toast.LENGTH_LONG).show()
        }

        pauseLL!!.setOnClickListener {
            val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (pauseTV!!.text.equals("Pause")) {
                    acceptRejectJob(job_id!!, "S")
                } else {
                    acceptRejectJob(job_id!!, "I")
                }
            }
            else
                Toast.makeText(baseActivity,"Please enable location services to start a job",Toast.LENGTH_LONG).show()
        }
    }

    private fun getJobDetailApi(job_id: String?) {
        baseActivity.showProgressDialog(baseActivity)

        val call = RetrofitHandler
                .getInstance()
                .api
                .getJobDetail(job_id,baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<JobDetail> {
            override fun onResponse(call: Call<JobDetail>, response: Response<JobDetail>) {
                baseActivity.hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {
                    baseActivity.hideProgressDialog()
                    var obj1:JobDetail? = response.body()
                    setNewData2(obj1!!)
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        baseActivity.goToLoginActivity()
                    } else {
                        baseActivity.showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<JobDetail>, t: Throwable) {
                baseActivity.hideProgressDialog()
               // baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(baseActivity,
                        ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(baseActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(baseActivity,
                            ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(baseActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(baseActivity,
                        arrayOf(ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
                        100)
            } else {
                requestPermissions(baseActivity,
                        arrayOf(ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
                        100)
            }
            return false
        } else {
            return true
        }
    }


    private fun acceptRejectJob(id: String,status:String) {
        val call = RetrofitHandler
                .getInstance()
                .api
                .acceptRejectJobs(id,status,baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<AcceptRejectData> {
            override fun onResponse(call: Call<AcceptRejectData>, response: Response<AcceptRejectData>) {
                if (response.code() == Constants.STATUS_OK) {
                    if(response.body()!!.message.equals("successfully rejected")) {
                        baseActivity.showToast("Job Request is successfully Rejected",false)
                        val intent = Intent(baseActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        setNewData1(response.body()!!.data)
                    }
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        baseActivity.goToLoginActivity()
                    } else {
                        baseActivity.showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<AcceptRejectData>, t: Throwable) {
                //baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }

    private fun setNewData2(data: JobDetail) {
        var jobsListData= data.data
        latLngList2 = jobsListData.locations as ArrayList<JobDetail.Locations>
        mapFragment!!.getMapAsync { mMap ->
            if (checkLocationPermission()) {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.clear()
                var lat_i = 0.0
                var longi_i = 0.0
                var latLngPl: ArrayList<LatLng> = ArrayList()
                for (i in latLngList2!!) {
                    lat_i = lat_i + i.latitude.toDouble()
                    longi_i = longi_i + i.longitude.toDouble()
                    latLngPl.add(LatLng(i.latitude.toDouble(), i.longitude.toDouble()))
                }
                var latLng =  LatLng(baseActivity.store!!.getString("currentLatitude")!!.toDouble(),baseActivity.store!!.getString("currentLongitude")!!.toDouble())
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isZoomControlsEnabled = true
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(lat_i / latLngList2.size, longi_i / latLngList2.size), 5F)
                mMap.animateCamera(cameraUpdate)
                var rectOptions = PolygonOptions().strokeColor(Color.BLUE).strokeWidth(2F).fillColor(Color.parseColor("#90BCDDE9"))
                        .addAll(latLngPl)
                mMap.addPolygon(rectOptions)

            }
        }
        if (map!=null){
            var points: ArrayList<LatLng> = ArrayList()
            for (item  in latLngList2) {
                var latitude = item.latitude
                var longitude = item.longitude
                val position = LatLng(latitude.toDouble(), longitude.toDouble())
                points.add(position)
            }

            lineOptions!!.addAll(points)
            lineOptions!!.width(5F)
            lineOptions!!.color(Color.RED)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 0F))
            if(lineOptions != null) {
                map!!.addPolyline(lineOptions)
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }

        jobIV!!.setOnClickListener {
            baseActivity.showImageDialog(jobsListData!!.images)
        }
        job_id = jobsListData!!.id.toString()
        Glide.with(baseActivity)
                .load(Constants.IMAGE_BASE_URL + jobsListData!!.images)
                .apply(RequestOptions()
                        .dontAnimate()
                        .placeholder(R.drawable.ic_no_image_found)
                        .error(R.drawable.ic_no_image_found))
                .into(jobIV!!)
        job_id_tv!!.setText(jobsListData!!.job_number)
        if(jobsListData!!.created_at!=null) {
            job_date_tv!!.setText(changeDateFormat(jobsListData!!.created_at, "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy"))
        } else{
            job_date_tv!!.setText("")
        }
        quantity_flyers!!.setText(jobsListData!!.flyers.toString())
        if (jobsListData!!.status!!.equals("P")) {
            accptRejectLL!!.visibility = View.VISIBLE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        } else if (jobsListData!!.status!!.equals("A")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.VISIBLE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        } else if (jobsListData!!.status!!.equals("I")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.VISIBLE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.setText("Your job is in progress")
            jobProgressTV!!.setTextColor(resources.getColor(R.color.green))
            jobProgressTV!!.visibility = View.VISIBLE
        } else if (jobsListData!!.status!!.equals("S")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.VISIBLE
            jobProgressTV!!.setText("Your job is paused")
            jobProgressTV!!.setTextColor(resources.getColor(R.color.grey))
            pauseTV!!.setText("Resume")
            jobProgressTV!!.visibility = View.VISIBLE
        } else if (jobsListData!!.status!!.equals("C")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        }

    }

    private fun setNewData1(jobsListData: AcceptRejectData.Data) {
        latLngList1 = jobsListData.locations
        mapFragment!!.getMapAsync { mMap ->
            if (checkLocationPermission()) {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.clear()
                var lat_i = 0.0
                var longi_i = 0.0
                var latLngPl: ArrayList<LatLng> = ArrayList()
                for (i in latLngList1!!) {
                    lat_i = lat_i + i.latitude.toDouble()
                    longi_i = longi_i + i.longitude.toDouble()
                    latLngPl.add(LatLng(i.latitude.toDouble(), i.longitude.toDouble()))
                }
                var latLng =  LatLng(baseActivity.store!!.getString("currentLatitude")!!.toDouble(),baseActivity.store!!.getString("currentLongitude")!!.toDouble())
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isZoomControlsEnabled = true
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(lat_i / latLngList1.size, longi_i / latLngList1.size), 9F)
                mMap.animateCamera(cameraUpdate)
                var rectOptions = PolygonOptions().strokeColor(Color.BLUE).strokeWidth(2F).fillColor(Color.parseColor("#90BCDDE9"))
                        .addAll(latLngPl)
                mMap.addPolygon(rectOptions)
            }
        }
        if (map!=null){
            var points: ArrayList<LatLng> = ArrayList()
            for (item  in latLngList1) {
                var latitude = item.latitude
                var longitude = item.longitude
                val position = LatLng(latitude.toDouble(), longitude.toDouble())
                points.add(position)
            }

            lineOptions!!.addAll(points)
            lineOptions!!.width(5F)
            lineOptions!!.color(Color.RED)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 0F))
            if(lineOptions != null) {
                map!!.addPolyline(lineOptions)
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
        jobIV!!.setOnClickListener {
            baseActivity.showImageDialog(jobsListData!!.images)
        }
        job_id = jobsListData!!.id.toString()
        Glide.with(baseActivity)
                    .load(Constants.IMAGE_BASE_URL + jobsListData!!.images)
                    .apply(RequestOptions()
                            .dontAnimate()
                            .placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(jobIV!!)
        job_id_tv!!.setText(jobsListData!!.job_number)
        if(jobsListData!!.created_at!=null) {
            job_date_tv!!.setText(changeDateFormat(jobsListData!!.created_at, "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy"))
        } else{
            job_date_tv!!.setText("")
        }
        quantity_flyers!!.setText(jobsListData!!.flyers.toString())
        if (jobsListData!!.status!!.equals("P")) {
            accptRejectLL!!.visibility = View.VISIBLE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        } else if (jobsListData!!.status!!.equals("A")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.VISIBLE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        } else if (jobsListData!!.status!!.equals("I")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.VISIBLE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.setText("Your job is in progress")
            jobProgressTV!!.setTextColor(resources.getColor(R.color.green))
            jobProgressTV!!.visibility = View.VISIBLE
        } else if (jobsListData!!.status!!.equals("S")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.VISIBLE
            jobProgressTV!!.setText("Your job is paused")
            jobProgressTV!!.setTextColor(resources.getColor(R.color.grey))
            pauseTV!!.setText("Resume")
            jobProgressTV!!.visibility = View.VISIBLE
        } else if (jobsListData!!.status!!.equals("C")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        }

    }

    private fun setNewData(jobsListData: JobsListData.Data?) {
        setMap(jobsListData)
        jobIV!!.setOnClickListener {
            baseActivity.showImageDialog(jobsListData!!.images)
        }
        job_id = jobsListData!!.id
        Glide.with(baseActivity)
                .load(Constants.IMAGE_BASE_URL + jobsListData!!.images)
                .apply(RequestOptions()
                        .dontAnimate()
                        .placeholder(R.drawable.ic_no_image_found)
                        .error(R.drawable.ic_no_image_found))
                .into(jobIV!!)
        job_id_tv!!.setText(jobsListData!!.job_number)
        job_name!!.setText(jobsListData!!.job_name)
        job_date_tv!!.setText(changeDateFormat(jobsListData!!.created_at, "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy"))
        quantity_flyers!!.setText(jobsListData!!.flyers.toString())
        if (jobsListData!!.status!!.equals("P")) {
            accptRejectLL!!.visibility = View.VISIBLE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        } else if (jobsListData!!.status!!.equals("A")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.VISIBLE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        } else if (jobsListData!!.status!!.equals("I")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.VISIBLE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.setText("Your job is in progress")
            jobProgressTV!!.setTextColor(resources.getColor(R.color.green))
            jobProgressTV!!.visibility = View.VISIBLE
        } else if (jobsListData!!.status!!.equals("S")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.VISIBLE
            pauseTV!!.setText("Resume")
            jobProgressTV!!.setText("Your job is paused")
            jobProgressTV!!.setTextColor(resources.getColor(R.color.grey))
            jobProgressTV!!.visibility = View.VISIBLE
        } else if (jobsListData!!.status!!.equals("C")) {
            accptRejectLL!!.visibility = View.GONE
            startLL!!.visibility = View.GONE
            complte_pauseLL!!.visibility = View.GONE
            pauseTV!!.setText("Pause")
            jobProgressTV!!.visibility = View.GONE
        }
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

    fun setMap(jobsListData: JobsListData.Data?) {

        token = baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!
        latLngList = jobsListData!!.locations
        mapFragment!!.getMapAsync { mMap ->
            if (checkLocationPermission()) {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.clear()
                var lat_i = 0.0
                var longi_i = 0.0
                var latLngPl: ArrayList<LatLng> = ArrayList()
                for (i in latLngList!!) {
                    lat_i = lat_i + i.latitude.toDouble()
                    longi_i = longi_i + i.longitude.toDouble()
                    latLngPl.add(LatLng(i.latitude.toDouble(), i.longitude.toDouble()))
                }
                var latLng =  LatLng(baseActivity.store!!.getString("currentLatitude")!!.toDouble(),baseActivity.store!!.getString("currentLongitude")!!.toDouble())
                mMap.isMyLocationEnabled = true
                //mMap.uiSettings.isZoomControlsEnabled = true
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(lat_i / latLngList.size, longi_i / latLngList.size), 9F)
                mMap.animateCamera(cameraUpdate)
                var rectOptions = PolygonOptions().strokeColor(Color.BLUE).strokeWidth(2F).fillColor(Color.parseColor("#90BCDDE9"))
                        .addAll(latLngPl)
                mMap.addPolygon(rectOptions)

                imgZoom!!.setOnClickListener { view ->
                    baseActivity.goToMapActivity(baseActivity.store!!.getString("currentLatitude")!!,baseActivity.store!!.getString("currentLongitude")!!,jobsListData!!.job_id!!.toString(),token!!,jobsListData!!.user_id!!,latLngList!!)
                }
//                imgZoom.setOnMapClickListener(GoogleMap.OnMapClickListener {
////                    var obj = MapsActivity()
////                    obj.openMap(baseActivity)
//                    println("JOBID===>"+ jobsListData!!.job_id)
//                    baseActivity.goToMapActivity(baseActivity.store!!.getString("currentLatitude")!!,baseActivity.store!!.getString("currentLongitude")!!,jobsListData!!.job_id!!.toString(),token!!,jobsListData!!.user_id!!)
//
//                })
            }
        }
        if (map!=null){
            var lat="0"
            var lng="0"
            var points: ArrayList<LatLng> = ArrayList()
            for (item  in latLngList) {
                var latitude = item.latitude
                var longitude = item.longitude
                lat =latitude
                lng= longitude
             //   var longitude = item.longitude
                val position = LatLng(latitude.toDouble(), longitude.toDouble())
                points.add(position)
            }

            lineOptions!!.addAll(points)
            lineOptions!!.width(5F)
            lineOptions!!.color(Color.RED)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(), lng.toDouble()), 0F))
            if(lineOptions != null) {
                map!!.addPolyline(lineOptions)
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}
