package com.activedistribution.view.adapter

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.NotificationListData
import com.activedistribution.view.activities.BaseActivity
import com.activedistribution.view.activities.CommonActivity
import com.google.gson.Gson
import retrofit2.Callback

class NotificationsAdapter (internal var notificationList: List<NotificationListData.Data>, internal var itemClick: Callback<NotificationListData>, internal var mContext: BaseActivity) : RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {

    internal var isLoadingMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsAdapter.ItemViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_notification, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationsAdapter.ItemViewHolder, position: Int) {
            holder.layout!!.setOnClickListener {
                mContext.store!!.setBoolean("is_push",true)
                mContext.store!!.saveString("job_id",notificationList.get(position).job_id.toString())
                val intent = Intent(mContext, CommonActivity::class.java)
                intent.putExtra("is_push","is_push")
                mContext.startActivity(intent)
            }
            holder.messageTV!!.setText(notificationList.get(position).message)
            holder.timeTV!!.setText(mContext.changeDateFormat(notificationList.get(position).updated_at, "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy"))
    }

    fun setLoadingMore(loading: Boolean) {
        isLoadingMore = loading
    }

    class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageTV : TextView? =null
        var timeTV : TextView? =null
        var layout : LinearLayout? =null
        init {
            messageTV = itemView.findViewById(R.id.messageTV)as TextView
            timeTV = itemView.findViewById(R.id.timeTV)as TextView
            layout = itemView.findViewById(R.id.layout)as LinearLayout
        }
    }
}