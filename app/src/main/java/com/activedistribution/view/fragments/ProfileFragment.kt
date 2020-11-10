package com.activedistribution.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.activedistribution.R
import com.activedistribution.model.ProfileData
import com.activedistribution.utils.Constants
import com.activedistribution.view.activities.CommonActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : BaseFragment() {

    private var editIV: ImageView? = null
    private var idProofIV: ImageView? = null
    private var appBar: ImageView? = null
    private var userIV: CircleImageView? = null
    private var userNameTV: TextView? = null
    private var nameTV: TextView? = null
    private var emailTV: TextView? = null
    private var addressTV: TextView? = null
    private var phoneTV: TextView? = null
    private var zipcodeTV: TextView? = null
    private var dobTV: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        initUI(v)

        return v
    }

    private fun initUI(v: View) {
        editIV = v.findViewById(R.id.editIV)
        idProofIV = v.findViewById(R.id.idProofIV)
        appBar = v.findViewById(R.id.appBar)
        userIV = v.findViewById(R.id.userIV)
        userNameTV = v.findViewById(R.id.userNameTV)
        nameTV = v.findViewById(R.id.nameTV)
        emailTV = v.findViewById(R.id.emailTV)
        addressTV = v.findViewById(R.id.addressTV)
        phoneTV = v.findViewById(R.id.phoneTV)
        zipcodeTV = v.findViewById(R.id.zipcodeTV)
        dobTV = v.findViewById(R.id.dobTV)

        editIV!!.setOnClickListener {
            val intent = Intent(activity, CommonActivity::class.java)
            intent.putExtra("edit", "edit")
            startActivity(intent)
        }
        val profileData = baseActivity.store!!.getProfileData()
        setData(profileData)
    }

    override fun onResume() {
        super.onResume()
        baseActivity.store!!.setBoolean("edit_back",true)
        val profileData = baseActivity.store!!.getProfileData()
        setData(profileData)
    }

    private fun setData(response: ProfileData?) {
        userIV!!.setOnClickListener {
            baseActivity.showImageDialog(response!!.image)
        }
        idProofIV!!.setOnClickListener {
            baseActivity.showImageDialog(response!!.attach_id)
        }
        baseActivity.hideProgressDialog()
        if(response!!.firstname==null) {
            response!!.firstname=""
            response!!.lastname=""
        }
        nameTV!!.setText(response!!.firstname + " "+response!!.lastname)
        userNameTV!!.setText(response!!.firstname + " "+response!!.lastname)
        emailTV!!.setText(response!!.email)
        addressTV!!.setText(response!!.address)
        phoneTV!!.setText(response!!.phone)
        zipcodeTV!!.setText(response!!.zipcode)
        if(response!!.dob=="0000-00-00") {
            response!!.dob=""
        }
        dobTV!!.setText(response!!.dob)
        if(response!!.image!=null) {
            Glide.with(baseActivity)
                    .load(Constants.IMAGE_BASE_URL + response.image)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(userIV!!)

        }
        if(response!!.image!=null) {
            Glide.with(baseActivity)
                    .load(Constants.IMAGE_BASE_URL + response.image)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(appBar!!)
        }
        if(response!!.attach_id!=null) {
            Glide.with(baseActivity)
                    .load(Constants.IMAGE_BASE_URL + response.attach_id)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(idProofIV!!)
        }
    }
}
