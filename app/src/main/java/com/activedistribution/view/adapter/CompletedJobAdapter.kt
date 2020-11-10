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

class CompletedJobAdapter (internal var completedJobsList : List<JobsListData.Data>,internal var context:BaseActivity,internal var onItemClick : onRecyclerViewItemClick):RecyclerView.Adapter<CompletedJobAdapter.ItemViewHolder>(){

    internal var isLoadingMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var  view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_completed_job,parent,false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return completedJobsList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position == completedJobsList.size-1){
            holder.live_view!!.visibility = View.GONE
        }
        holder.layout!!.setOnClickListener {
            context.store!!.setBoolean("is_push",false)
            val gson = Gson()
            val json = gson!!.toJson(completedJobsList.get(position))
            context.store!!.saveString("jobsListData",json)
            onItemClick.onItemClick(position)
        }
        holder.job_id_tv!!.setText((completedJobsList.get(position).job_number))
        holder.job_name!!.setText((completedJobsList.get(position).job_name))
        holder.date_tv!!.setText(context.changeDateFormat(completedJobsList.get(position).created_at,"yyyy-MM-dd HH:mm:ss","MMM dd, yyyy"))
        holder.quantity_tv!!.setText(completedJobsList.get(position).flyers.toString())
        holder.job_desc!!.setText("Your job is in completed")
       // if(completedJobsList.get(position).images.size!=0) {
            Glide.with(context)
                    .load(Constants.IMAGE_BASE_URL + completedJobsList.get(position).images)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(holder.job_iv!!)
      //  }
        holder.job_iv!!.setOnClickListener {
            context.showImageDialog(completedJobsList.get(position).images)
        }
    }

    fun setLoadingMore(loading: Boolean) {
        isLoadingMore = loading
    }

    class ItemViewHolder(itemView :View) : RecyclerView.ViewHolder(itemView) {
        var live_view  : View? = null
        var layout : RelativeLayout ? =null
        var job_id_tv : TextView? =null
        var date_tv : TextView? =null
        var quantity_tv : TextView? =null
        var job_desc : TextView? =null
        var job_iv : RectCornerImageView? =null
        var job_name:TextView?=null
        init{
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