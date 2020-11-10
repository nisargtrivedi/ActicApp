package com.activedistribution.view.fragments

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.AcceptRejectData
import com.activedistribution.model.JobsListData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.MainApplication
import com.activedistribution.utils.Utils
import com.activedistribution.view.activities.CommonActivity
import com.activedistribution.view.adapter.NewJobsAdapter
import com.activedistribution.view.adapterintefrace.newJobInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NewJobFragment : BaseFragment(), newJobInterface {

    var job_rv: RecyclerView? = null
    var noDataFoundTV: TextView? = null
    internal var newJobsList: ArrayList<JobsListData.Data> = ArrayList()
    var newJobAdapter: NewJobsAdapter? = null
    //Page
    internal var page = 0
    internal var pastVisiblesItems: Int = 0
    internal var totalItemCount:Int = 0
    internal var visibleItemCount:Int = 0
    internal var loading = true


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_new_job, container, false)
        init(view)
         baseActivity.store!!.setBoolean("edit_back",false)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (!baseActivity.store!!.getBoolean("edit_back")) {
            baseActivity.store!!.setBoolean("edit_back", false)
            getNewJobsList(page)
        } else {
            baseActivity.store!!.setBoolean("edit_back",false)
        }
    }
    private fun init(view: View?) {
        job_rv = view!!.findViewById(R.id.job_rv) as RecyclerView
        noDataFoundTV = view!!.findViewById(R.id.noDataFoundTV) as TextView
        job_rv!!.setHasFixedSize(true)
        job_rv!!.layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)

        job_rv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val mLayoutManager = recyclerView!!.layoutManager as LinearLayoutManager
                visibleItemCount = mLayoutManager.childCount
                totalItemCount = mLayoutManager.itemCount
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                if (loading) {
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                        loading = false
                        callPagenation()
                    }
                }
            }
        })
    }


    private fun callPagenation() {
        var page1 = page?.plus(1)
        getNewJobsList(page1)
    }

    override fun onItemClick(pos: Int, key: String,id:String) {
        if(key.equals("Accept")) {
            acceptRejectJob(id,"A")
        } else if (key.equals("Reject")) {
            AlertDialog.Builder(baseActivity)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to Reject this job?")
                    .setPositiveButton("Yes", DialogInterface.OnClickListener {
                        dialog, which ->  acceptRejectJob(id,"R")
                    })
                    .setNegativeButton("No", null)
                    .show()

        } else {
            val intent = Intent(activity, CommonActivity::class.java)
            intent.putExtra("NewRequest", "NewRequest")
            startActivity(intent)
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
                        baseActivity.loadFragment(NewJobFragment(), R.id.job_frame)
                    } else {
                        if(response.body()!!.data.status.equals("A")) {
                            val fm = fragmentManager
                            val parentFrag = fm!!.findFragmentById(R.id.frame_layout_new) as HomeFragment
                            parentFrag!!.change_bg_bolor(parentFrag!!.tv_new_jobs!!, 2)
                            parentFrag!!.change_bg_bolor(parentFrag!!.tv_current_jobs!!, 1)
                            parentFrag!!.change_bg_bolor(parentFrag!!.tv_complete_jobs!!, 2)
                            baseActivity.loadFragment(CurrentJobFragment(), R.id.job_frame)
                     }
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
                baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }


    private fun getNewJobsList(pagee:Int) {
        baseActivity.showProgressDialog(baseActivity)
        val call = RetrofitHandler
                .getInstance()
                .api
                .getNewJobsList(pagee.toString(),baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<JobsListData> {
            override fun onResponse(call: Call<JobsListData>, response: Response<JobsListData>) {
                baseActivity.hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {
                        if (pagee ==0){
                            if (newJobsList!= null){
                                newJobsList.clear()
                            }
                            newJobsList.addAll(response.body()!!.data)
                        }else{
                            newJobsList.addAll(response.body()!!.data)
                        }

                       if(!newJobsList.isEmpty()) {
                           noDataFoundTV!!.visibility=View.GONE
                           job_rv!!.visibility = View.VISIBLE
                        var fragment = baseActivity.supportFragmentManager.findFragmentById(R.id.frame_layout_new) as HomeFragment
                        fragment.notificationCount(newJobsList.get(0).notification_count)
                        if (newJobsList.size == 10) {
                            loading = true
                        } else {
                            loading = false
                        }
                        if (newJobAdapter == null) {
                            setAdapter()
                        } else {
                            newJobAdapter!!.notifyDataSetChanged()
                        }
                        newJobAdapter!!.setLoadingMore(loading)
                    } else {
                           if(pagee==0) {
                               noDataFoundTV!!.visibility = View.VISIBLE
                               job_rv!!.visibility = View.GONE
                           }
                    }
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        baseActivity.goToLoginActivity()
                        baseActivity.hideProgressDialog()
                    } else {
                        baseActivity.hideProgressDialog()
                        baseActivity.showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<JobsListData>, t: Throwable) {
                baseActivity.hideProgressDialog()
               // baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }

    fun setAdapter() {
        newJobAdapter = NewJobsAdapter(newJobsList, this, baseActivity)
        job_rv!!.adapter = newJobAdapter
    }
}


