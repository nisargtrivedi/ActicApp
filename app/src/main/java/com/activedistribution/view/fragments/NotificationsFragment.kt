package com.activedistribution.view.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.NotificationListData
import com.activedistribution.retrofitManager.RetrofitHandler
import com.activedistribution.utils.Constants
import com.activedistribution.utils.MainApplication
import com.activedistribution.utils.Utils
import com.activedistribution.view.adapter.NotificationsAdapter
import com.activedistribution.view.adapterintefrace.onRecyclerViewItemClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsFragment : BaseFragment(), onRecyclerViewItemClick {

    var notifications_rv: RecyclerView? = null
    var noDataFoundTV: TextView? = null
    var iv_back: ImageView? = null
    internal var notificationList: java.util.ArrayList<NotificationListData.Data> = ArrayList()
    var notificationsAdapter: NotificationsAdapter? = null
    //Page
    internal var page = 0
    internal var pastVisiblesItems: Int = 0
    internal var totalItemCount:Int = 0
    internal var visibleItemCount:Int = 0
    internal var loading = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_notifications, container, false)
        initUI(v)
        return v
    }

    private fun initUI(v: View?) {
        notifications_rv = v!!.findViewById(R.id.notifications_rv) as RecyclerView
        noDataFoundTV = v!!.findViewById(R.id.noDataFoundTV) as TextView
        iv_back = v!!.findViewById(R.id.iv_back) as ImageView
        notifications_rv!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(baseActivity)
        notifications_rv!!.layoutManager = mLayoutManager
        iv_back!!.setOnClickListener {
            activity!!.onBackPressed()
        }
        getNotificationsList(page)
        notifications_rv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        getNotificationsList(page1)
    }


    private fun getNotificationsList(pagee:Int) {
        baseActivity.showProgressDialog(baseActivity)
        val call = RetrofitHandler
                .getInstance()
                .api
                .getNotificationsList(pagee.toString(),baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!)

        call.enqueue(object : Callback<NotificationListData> {
            override fun onResponse(call: Call<NotificationListData>, response: Response<NotificationListData>) {
                baseActivity.hideProgressDialog()
                if (response.code() == Constants.STATUS_OK) {

                    notificationList.addAll(response.body()!!.data)
                    if(!notificationList.isEmpty()) {
                        noDataFoundTV!!.visibility = View.GONE
                        notifications_rv!!.visibility = View.VISIBLE

                        if (notificationList.size == 10) {
                            loading = true
                        } else {
                            loading = false
                        }
                        if (notificationsAdapter == null) {
                            notificationsAdapter = NotificationsAdapter(notificationList, this, baseActivity)
                            notifications_rv!!.adapter = notificationsAdapter
                        } else {
                            notificationsAdapter!!.notifyDataSetChanged()
                        }
                        notificationsAdapter!!.setLoadingMore(loading)
                    } else {
                        noDataFoundTV!!.visibility = View.VISIBLE
                        notifications_rv!!.visibility = View.GONE
                    }
                } else {
                    if(response.code()==Constants.UNAUTHORIZED) {
                        baseActivity.goToLoginActivity()
                    } else {
                        baseActivity.showToast(Utils.onResoinseError(response), false)
                    }
                }
            }

            override fun onFailure(call: Call<NotificationListData>, t: Throwable) {
                baseActivity.hideProgressDialog()
               // baseActivity.showToast(MainApplication.getInstance().getString(R.string.on_dailure_error),false)
            }
        })
    }

    override fun onItemClick(pos: Int) {

    }
}
