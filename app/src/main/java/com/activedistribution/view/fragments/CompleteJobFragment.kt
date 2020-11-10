package com.activedistribution.view.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.JobsListData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.MainApplication
import com.activedistribution.utils.Utils
import com.activedistribution.view.activities.CommonActivity
import com.activedistribution.view.adapter.CompletedJobAdapter
import com.activedistribution.view.adapterintefrace.onRecyclerViewItemClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompleteJobFragment : BaseFragment(), onRecyclerViewItemClick {

    var job_rv: RecyclerView? = null
    var noDataFoundTV: TextView? = null
    internal var completedJobsList: ArrayList<JobsListData.Data> = ArrayList()
    var completed_job_adapter : CompletedJobAdapter?=null
    //Page
    internal var page = 0
    internal var pastVisiblesItems: Int = 0
    internal var totalItemCount:Int = 0
    internal var visibleItemCount:Int = 0
    internal var loading = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_new_job, container, false)
        init(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        getcompletedJobsList(page)
    }

    private fun init(view: View?) {
        job_rv = view!!.findViewById(R.id.job_rv) as RecyclerView
        noDataFoundTV = view!!.findViewById(R.id.noDataFoundTV) as TextView
        job_rv!!.setHasFixedSize(true)
        job_rv!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

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
        getcompletedJobsList(page1)
    }

    private fun getcompletedJobsList(pagee:Int) {
        baseActivity.showProgressDialog(baseActivity)
        val call = RetrofitHandler
                .getInstance()
                .api
                .getCompletedJobsList(pagee.toString(),baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<JobsListData> {
            override fun onResponse(call: Call<JobsListData>, response: Response<JobsListData>) {
                baseActivity.hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {

                    if (pagee ==0){
                        if (completedJobsList!= null){
                            completedJobsList.clear()
                        }
                        completedJobsList.addAll(response.body()!!.data)
                    }else{
                        completedJobsList.addAll(response.body()!!.data)
                    }
                   // completedJobsList.addAll(response.body()!!.data)
                    if(!completedJobsList.isEmpty()) {
                        noDataFoundTV!!.visibility = View.GONE
                        job_rv!!.visibility = View.VISIBLE
                        if (completedJobsList.size == 10) {
                            loading = true
                        } else {
                            loading = false
                        }

                        if (completed_job_adapter == null) {
                            setAdapter()
                        } else {
                            completed_job_adapter!!.notifyDataSetChanged()
                        }
                        completed_job_adapter!!.setLoadingMore(loading)
                    } else {
                        if(pagee==0) {
                            noDataFoundTV!!.visibility = View.VISIBLE
                            job_rv!!.visibility = View.GONE
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

            override fun onFailure(call: Call<JobsListData>, t: Throwable) {
                baseActivity.hideProgressDialog()
              //  baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }

    fun setAdapter() {
        completed_job_adapter = CompletedJobAdapter(completedJobsList, baseActivity, this)
        job_rv!!.adapter = completed_job_adapter
    }


    override fun onItemClick(pos: Int) {
        val intent = Intent(activity, CommonActivity::class.java)
        intent.putExtra("Completed", "Completed")
        startActivity(intent)
    }
}