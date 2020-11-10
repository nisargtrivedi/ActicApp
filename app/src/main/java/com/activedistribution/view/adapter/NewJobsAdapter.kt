package com.activedistribution.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.JobsListData
import com.activedistribution.utils.Constants
import com.activedistribution.utils.RectCornerImageView
import com.activedistribution.view.activities.BaseActivity
import com.activedistribution.view.adapterintefrace.newJobInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

class NewJobsAdapter(internal var newJobsList: List<JobsListData.Data>, internal var itemClick: newJobInterface, internal var baseActivity: BaseActivity) : RecyclerView.Adapter<NewJobsAdapter.ItemViewHolder>() {

    internal var isLoadingMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewJobsAdapter.ItemViewHolder {
        var view =LayoutInflater.from(parent.context).inflate(R.layout.adapter_new_jobs, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newJobsList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position == newJobsList.size-1) {
            holder.live_view!!.visibility = View.GONE
        }
        holder.layout!!.setOnClickListener {
            baseActivity.store!!.setBoolean("is_push",false)
            val gson = Gson()
            val json = gson.toJson(newJobsList.get(position))
            baseActivity.store!!.saveString("jobsListData",json)
            itemClick.onItemClick(position,"","")
        }
        holder.acceptLL!!.setOnClickListener {
            itemClick.onItemClick(position,"Accept",newJobsList.get(position).id)
        }
        holder.rejectLL!!.setOnClickListener {
            itemClick.onItemClick(position,"Reject",newJobsList.get(position).id)
        }
        holder.job_id_tv!!.setText((newJobsList.get(position).job_number))
        holder.job_name!!.setText((newJobsList.get(position).job_name))
        holder.quantity_tv!!.setText(newJobsList.get(position).flyers.toString())
        holder.date_tv!!.setText(baseActivity.changeDateFormat(newJobsList.get(position).created_at,"yyyy-MM-dd HH:mm:ss","MMM dd, yyyy"))
        Glide.with(baseActivity)
                .load(Constants.IMAGE_BASE_URL + newJobsList.get(position).images)
                .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                        .error(R.drawable.ic_no_image_found))
                .into(holder.job_iv!!)
        holder.job_iv!!.setOnClickListener {
            baseActivity.showImageDialog(newJobsList.get(position).images)
        }
    }

    fun setLoadingMore(loading: Boolean) {
        isLoadingMore = loading
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var live_view  : View? = null
        var layout : RelativeLayout? =null
        var job_id_tv : TextView? =null
        var date_tv : TextView? =null
        var job_name:TextView?=null
        var quantity_tv : TextView? =null
        var acceptLL : LinearLayout? =null
        var rejectLL : LinearLayout? =null
        var job_iv : RectCornerImageView? =null

        init {
            live_view = itemView.findViewById(R.id.live_view)as View
            layout = itemView.findViewById(R.id.layout)as RelativeLayout
            acceptLL = itemView.findViewById(R.id.acceptLL)as LinearLayout
            rejectLL = itemView.findViewById(R.id.rejectLL)as LinearLayout
            job_iv = itemView.findViewById(R.id.job_iv)as RectCornerImageView
            job_id_tv = itemView.findViewById(R.id.job_id_tv)as TextView
            date_tv = itemView.findViewById(R.id.date_tv)as TextView
            quantity_tv = itemView.findViewById(R.id.quantity_tv)as TextView
            job_name = itemView.findViewById(R.id.job_name)as TextView

        }

    }
}
