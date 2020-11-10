package com.activedistribution.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.view.activities.CommonActivity


class HomeFragment : BaseFragment() {

    var tv_new_jobs: TextView? = null
    var tv_current_jobs: TextView? = null
    var tv_complete_jobs: TextView? = null
    var notificationCountTV: TextView? = null
    var iv_notification: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false);
        baseActivity.loadFragment(NewJobFragment(),R.id.job_frame)
        init(view)
        onClick()
        return view
    }

    private fun onClick() {
        tv_new_jobs!!.setOnClickListener {
            change_bg_bolor(tv_new_jobs!!, 1)
            change_bg_bolor(tv_current_jobs!!, 2)
            change_bg_bolor(tv_complete_jobs!!, 2)
            baseActivity.loadFragment(NewJobFragment(),R.id.job_frame)
        }
        tv_current_jobs!!.setOnClickListener {
            change_bg_bolor(tv_new_jobs!!, 2)
            change_bg_bolor(tv_current_jobs!!, 1)
            change_bg_bolor(tv_complete_jobs!!, 2)
            baseActivity.loadFragment(CurrentJobFragment(),R.id.job_frame)
        }
        tv_complete_jobs!!.setOnClickListener {
            change_bg_bolor(tv_new_jobs!!, 2)
            change_bg_bolor(tv_current_jobs!!, 2)
            change_bg_bolor(tv_complete_jobs!!, 1)
            baseActivity.loadFragment(CompleteJobFragment(),R.id.job_frame)
        }
        iv_notification!!.setOnClickListener {
            val intent = Intent(activity, CommonActivity::class.java)
            intent.putExtra("notification","notification")
            startActivity(intent)
        }
    }


    private fun init(view: View) {
        tv_new_jobs = view.findViewById(R.id.tv_new_jobs) as TextView
        tv_current_jobs = view.findViewById(R.id.tv_current_jobs) as TextView
        tv_complete_jobs = view.findViewById(R.id.tv_complete_jobs) as TextView
        iv_notification = view.findViewById(R.id.iv_notification) as ImageView
        notificationCountTV = view.findViewById(R.id.notificationCountTV) as TextView
    }


     fun change_bg_bolor(textView: TextView, i: Int) {
        if (i == 1) {
            textView.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient_button))
            textView.setTextColor(resources.getColor(R.color.White))
        } else{
            textView.setBackgroundColor(resources.getColor(android.R.color.transparent))
            textView.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        }
    }

    fun notificationCount(notificationCount: String) {
        notificationCountTV!!.setText(notificationCount)

    }
}




