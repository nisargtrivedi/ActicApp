package com.activedistribution.view.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.activedistribution.R
import com.activedistribution.model.CreateProfileData
import com.activedistribution.model.ProfileData
import com.activedistribution.presenter.EditProfile.EditProfileInterface
import com.activedistribution.presenter.EditProfile.EditProfilePresenter
import com.activedistribution.utils.Constants
import com.activedistribution.utils.ImageUtils
import com.activedistribution.view.activities.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*


class EditProfileFragment : BaseFragment(), View.OnClickListener, EditProfileInterface,BaseActivity.PermCallback,ImageUtils.ImageSelectCallback {

    override fun onImageSelected(imagePath: String?, resultCode: Int) {
    }

     var userIV: ImageView? = null
     var firstNameET: EditText? = null
     var lastNameET: EditText? = null
     var phoneET: EditText? = null
     var addressET: EditText? = null
     var zipcodeET: EditText? = null
     var dobET: EditText? = null
     var updateProfileBT: Button? = null
     var iv_back: ImageView? = null
     var layoutLL: LinearLayout? = null
     var presenter: EditProfilePresenter? = null
     private var year: Int = 0
     private var month: Int = 0
     private var day: Int = 0
     private var bday: String? = ""
     private var updated_on: String? = null
    var currentTime : Date?=null
    var idProofIV : ImageView?=null
    private var user: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        presenter = EditProfilePresenter(this)
        initUI(v)
        return v
    }

    override fun onResume() {
        super.onResume()
        baseActivity.store!!.setBoolean("edit_back",true)
    }

    private fun initUI(v: View) {
        userIV = v.findViewById(R.id.userIV) as ImageView
        idProofIV = v.findViewById(R.id.idProofIV) as ImageView
        iv_back = v.findViewById(R.id.iv_back) as ImageView
        firstNameET = v.findViewById(R.id.firstNameET) as EditText
        lastNameET = v.findViewById(R.id.lastNameET) as EditText
        phoneET = v.findViewById(R.id.phoneET) as EditText
        addressET = v.findViewById(R.id.addressET) as EditText
        zipcodeET = v.findViewById(R.id.zipcodeET) as EditText
        layoutLL = v.findViewById(R.id.layoutLL) as LinearLayout
        dobET = v.findViewById(R.id.dobET) as EditText
        updateProfileBT = v.findViewById(R.id.updateProfileBT) as Button
        updateProfileBT!!.setOnClickListener(this)
        userIV!!.setOnClickListener(this)
        idProofIV!!.setOnClickListener(this)
        dobET!!.setOnClickListener(this)
        addressET!!.setOnClickListener(this)
        iv_back!!.setOnClickListener(this)
        firstNameET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        lastNameET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        //phoneET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        addressET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        zipcodeET!!.setFilters(arrayOf(baseActivity.noSpaceFilter))
        layoutLL!!.setOnTouchListener(View.OnTouchListener { view, ev ->
            baseActivity.hideSoftKeyboard(view)
            false
        })
        currentTime = Calendar.getInstance().time
        updated_on = currentTime.toString()
        val profileData = baseActivity.store!!.getProfileData()
        if(profileData.address==null) {
            profileData.address=""
        }
        setData(profileData)
    }


    private fun setData(response: ProfileData?) {
        firstNameET!!.setText(response!!.firstname)
        lastNameET!!.setText(response!!.lastname)
        addressET!!.setText(response!!.address)
        phoneET!!.setText(response!!.phone)
        zipcodeET!!.setText(response!!.zipcode)
        if(response!!.dob.equals("0000-00-00")) {
            dobET!!.setText("")
        } else {
            dobET!!.setText(response!!.dob)
        }

        bday = response!!.dob

        if(response!!.image!=null&& !response!!.attach_id.equals("")) {
            Glide.with(baseActivity)
                    .load(Constants.IMAGE_BASE_URL + response.image)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(userIV!!)
             var policy =  StrictMode.ThreadPolicy.Builder().permitAll().build();
    	       StrictMode.setThreadPolicy(policy)
            try {
                val url = URL(Constants.IMAGE_BASE_URL + response.image)
                var bm = BitmapFactory.decodeStream(url.content as InputStream)
                user_pic_file =  ImageUtils.bitmapToFile(bm!!,baseActivity)
            } catch (e: IOException) {
               e.printStackTrace()
            }
        } else {
            Glide.with(baseActivity)
                    .load(R.drawable.ic_no_image_found)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(idProofIV!!)
        }

        if(response!!.attach_id!=null && !response!!.attach_id.equals("")) {
            Glide.with(baseActivity)
                    .load(Constants.IMAGE_BASE_URL + response.attach_id)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(idProofIV!!)
            var policy =  StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy)
            try {
                val url = URL(Constants.IMAGE_BASE_URL + response.attach_id)
                var bm = BitmapFactory.decodeStream(url.content as InputStream)
                proof_file =  ImageUtils.bitmapToFile(bm!!,baseActivity)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Glide.with(baseActivity)
                    .load(R.drawable.ic_no_image_found)
                    .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                            .error(R.drawable.ic_no_image_found))
                    .into(idProofIV!!)
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.updateProfileBT -> {
                val firstName = firstNameET!!.text.toString().trim { it <= ' ' }
                val lastName = lastNameET!!.text.toString().trim { it <= ' ' }
                val phone = phoneET!!.text.toString().trim { it <= ' ' }
                val address = addressET!!.text.toString().trim { it <= ' ' }
                val zipcode = zipcodeET!!.text.toString().trim { it <= ' ' }
                if(user_pic_file==null) {
                    baseActivity.showToast("You cannot leave user image empty",false)
                } else if( proof_file==null) {
                    baseActivity.showToast("You cannot leave proof image empty",false)
                }  else {
                    presenter!!.checkValidation(firstName, lastName, phone, address, zipcode, bday!!, baseActivity.store!!.getString(Constants.REMEMBER_TOKEN)!!, user_pic_file!!,proof_file!!, updated_on!!)
                }
            }
            R.id.dobET -> {
                datePicker()
            } R.id.addressET -> {
            baseActivity.store!!.setBoolean("places",true)
            try {
                val intent1 = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(baseActivity)
                baseActivity.startActivityForResult(intent1, 1)
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            }
        }
            R.id.userIV -> {
                baseActivity.store!!.setBoolean("places",false)
                user = true
                if (baseActivity.checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 200, this)) {
                    ImageUtils.ImageSelect.Builder(baseActivity, this as ImageUtils.ImageSelectCallback, 200).start()
                }
            }
            R.id.idProofIV -> {
                baseActivity.store!!.setBoolean("places",false)
                user = false
                if (baseActivity.checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 200, this)) {
                    ImageUtils.ImageSelect.Builder(baseActivity, this as ImageUtils.ImageSelectCallback, 200).start()
                }
            }
            R.id.iv_back -> {
                activity!!.onBackPressed()
            }
        }
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR,2000)
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(baseActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dobET!!.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    bday = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                }, year, month, day)
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    override fun onSuccess(message: CreateProfileData) {
        baseActivity.store!!.setProfileData(message.user_info as ProfileData)
        activity!!.onBackPressed()
    }

    override fun onError(error: String) {
        baseActivity.showToast(error, false)
    }

    override fun onUnauthorised(error: String) {
         baseActivity.goToLoginActivity()
    }

    override fun permGranted(resultCode: Int) {

    }

    override fun permDenied(resultCode: Int) {

    }


    var thumbnail:Bitmap?= null
    var user_pic_file:File?= null
    var proof_file:File?= null

    fun setImage(data: Intent?,requestCode:Int?) {
        if (requestCode == 2) {
            if (data != null) {
                thumbnail = data!!.extras!!.get("data") as Bitmap
                if(user) {
                    Glide.with(baseActivity)
                            .load(thumbnail)
                            .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                                    .error(R.drawable.ic_no_image_found))
                            .into(userIV!!)
                    // creating file from camera
                    user_pic_file = ImageUtils.bitmapToFile(thumbnail!!,baseActivity)
                } else {
                    Glide.with(baseActivity)
                            .load(thumbnail)
                            .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                                    .error(R.drawable.ic_no_image_found))
                            .into(idProofIV!!)
                    // creating file from camera
                    proof_file = ImageUtils.bitmapToFile(thumbnail!!,baseActivity)
                }
            }
        } else {
            if (data != null) {
                val inputUri = data!!.data
                val imagePath = ImageUtils.getRealPath(baseActivity, inputUri)
                 thumbnail = ImageUtils.imageCompress(imagePath!!, 4000f, 3000f)
                if(user) {
                    Glide.with(baseActivity)
                            .load(thumbnail)
                            .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                                    .error(R.drawable.ic_no_image_found))
                            .into(userIV!!)
                    // creating file from gallery
                    var selectedImage = data!!.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = baseActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null)
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val filePath = cursor.getString(columnIndex)
                        user_pic_file = File(filePath)
                    }
                    cursor.close()
                } else {
                    Glide.with(baseActivity)
                            .load(thumbnail)
                            .apply(RequestOptions().dontAnimate().placeholder(R.drawable.ic_no_image_found)
                                    .error(R.drawable.ic_no_image_found))
                            .into(idProofIV!!)
                    // creating file from gallery
                    var selectedImage = data!!.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = baseActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null)
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val filePath = cursor.getString(columnIndex)
                        proof_file = File(filePath)
                    }
                    cursor.close()
                }
            }
        }
    }

    override fun onShowProgress() {
        baseActivity.showProgressDialog(baseActivity)
    }

    override fun onHideProgress() {
         baseActivity.hideProgressDialog()
    }

    fun setPlace(address: String) {
        addressET!!.setText(address)
    }
}
