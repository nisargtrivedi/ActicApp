package com.activedistribution.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.JobsListData
import com.activedistribution.utils.Constants
import com.activedistribution.utils.RectCornerImageView
import com.activedistribution.view.activities.BaseActivity
import com.activedistribution.view.adapterintefrace.onRecyclerViewItemClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

class CurrentJobAdapter (internal var currentJobsList : List<JobsListData.Data>, internal var itemClick: onRecyclerViewItemClick, internal var mContext: BaseActivity) : RecyclerView.Adapter<CurrentJobAdapter.ItemViewHolder>() {

    internal var isLoadingMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view =LayoutInflater.from(parent.context).inflate(R.layout.adapter_current_job, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currentJobsList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position == currentJobsList.size-1){
            holder.live_view!!.visibility = View.GONE
        }
        holder.layout!!.setOnClickListener {
            mContext.store!!.setBoolean("is_push",false)
            val gson = Gson()
            val json = gson!!.toJson(currentJobsList.get(position))
            mContext.store!!.saveString("jobsListData",json)
            itemClick.onItemClick(position)
        }
        holder.job_id_tv!!.setText((currentJobsList.get(position).job_number))
        holder.job_name!!.setText((currentJobsList.get(position).job_name))
        holder.date_tv!!.setText(mContext.changeDateFormat(currentJobsList.get(position).created_at,"yyyy-MM-dd HH:mm:ss","MMM dd, yyyy"))
        holder.quantity_tv!!.setText(currentJobsList.get(position).flyers.toString())
        if(currentJobsList.get(position).status.equals("I")) {
            holder.job_desc!!.setText("Your job is in progress")
            holder.job_desc!!.setTextColor(mContext.resources.getColor(R.color.green))
        } else if(currentJobsList.get(position).status.equals("S")) {
            holder.job_desc!!.setText("Your job is paused")
            holder.job_desc!!.setTextColor(mContext.resources.getColor(R.color.grey))
        } else if(currentJobsList.get(position).status.equals("A")) {
            holder.job_desc!!.setText("Your job is not started yet.")
            holder.job_desc!!.setTextColor(mContext.resources.getColor(R.color.grey))
        }
        //if(currentJobsList.get(position).images.size!=0) {
            Glide.with(mContext)
                    .load(Constants.IMAGE_BASE_URL + currentJobsList.get(position).images)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(holder.job_iv!!)
       // }
        holder.job_iv!!.setOnClickListener {
            mContext.showImageDialog(currentJobsList.get(position).images)
        }
    }

    fun setLoadingMore(loading: Boolean) {
        isLoadingMore = loading
    }

    class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var live_view  : View? = null
        var layout : RelativeLayout? =null
        var job_id_tv : TextView? =null
        var date_tv : TextView? =null
        var job_name:TextView?=null
        var quantity_tv : TextView? =null
        var job_desc : TextView? =null
        var job_iv : RectCornerImageView? =null

        init {
            live_view = itemView.findViewById(R.id.live_view)as View
            layout = itemView.findViewById(R.id.layout)as RelativeLayout
            job_iv = itemView.findViewById(R.id.job_iv)as RectCornerImageView
            job_id_tv = itemView.findViewById(R.id.job_id_tv)as TextView
            date_tv = itemView.findViewById(R.id.date_tv)as TextView
            quantity_tv = itemView.findViewById(R.id.quantity_tv)as TextView
            job_desc = itemView.findViewById(R.id.job_desc)as TextView
            job_name = itemView.findViewById(R.id.job_name)as TextView
        }
    }
}