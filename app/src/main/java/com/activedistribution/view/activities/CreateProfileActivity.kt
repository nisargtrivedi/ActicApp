package com.activedistribution.view.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.activedistribution.presenter.createProfile.createProfilePresenter
import com.activedistribution.utils.ImageUtils
import com.activedistribution.R
import com.activedistribution.presenter.createProfile.createProfileInterface
import java.io.File
import java.util.Calendar
import java.util.Date
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import com.activedistribution.model.CreateProfileData
import com.activedistribution.utils.Constants
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.activedistribution.model.ProfileData
import com.google.gson.Gson


class CreateProfileActivity : BaseActivity(), View.OnClickListener, BaseActivity.PermCallback, ImageUtils.ImageSelectCallback, createProfileInterface {


    private var userIV: ImageView? = null
    private var iv_back: ImageView? = null
    private var firstNameET: EditText? = null
    private var lastNameET: EditText? = null
    private var phoneET: EditText? = null
    private var addressET: EditText? = null
    private var dobET: EditText? = null
    private var zipcodeET: EditText? = null
    private var idproofET: EditText? = null
    private var browseBT: Button? = null
    private var createProfileBT: Button? = null
    private var user: Boolean = false
    private var places: Boolean = false
    private var presenter: createProfilePresenter? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var bday: String? = null
    private var  updated_on: String? = null
    private var user_pic_file: File? = null
    private var proof_file: File? = null
    private var layoutLL: LinearLayout? = null
    var currentTime : Date?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        presenter = createProfilePresenter(this)
        initUI()
    }

    private fun initUI() {
        userIV = findViewById(R.id.userIV)
        iv_back = findViewById(R.id.iv_back)
        firstNameET = findViewById(R.id.firstNameET)
        lastNameET = findViewById(R.id.lastNameET)
        phoneET = findViewById(R.id.phoneET)
        addressET = findViewById(R.id.addressET)
        zipcodeET = findViewById(R.id.zipcodeET)
        dobET = findViewById(R.id.dobET)
        idproofET = findViewById(R.id.idproofET)
        browseBT = findViewById(R.id.browseBT)
        layoutLL = findViewById(R.id.layoutLL)
        createProfileBT = findViewById(R.id.createProfileBT)
        browseBT!!.setOnClickListener(this)
        createProfileBT!!.setOnClickListener(this)
        userIV!!.setOnClickListener(this)
        iv_back!!.setOnClickListener(this)
        addressET!!.setOnClickListener(this)
        dobET!!.setOnClickListener(this)
       // firstNameET!!.setFilters(arrayOf(noSpaceFilter))
        lastNameET!!.setFilters(arrayOf(noSpaceFilter))
       // phoneET!!.setFilters(arrayOf(noSpaceFilter))
        zipcodeET!!.setFilters(arrayOf(noSpaceFilter))
        addressET!!.setFilters(arrayOf(noSpaceFilter))
        layoutLL!!.setOnTouchListener(OnTouchListener { view, ev ->
            hideSoftKeyboard(view)
            false
        })
         currentTime = Calendar.getInstance().time
         updated_on = currentTime.toString()

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.userIV -> {
                if (currentFocus != null) {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager!!.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
                places = false
                user = true
                if (checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 200, this)) {
                    ImageUtils.ImageSelect.Builder(this, this as ImageUtils.ImageSelectCallback, 200).start()
                }
            }
            R.id.createProfileBT -> {
                val firstName = firstNameET!!.text.toString().trim { it <= ' ' }
                val lastName = lastNameET!!.text.toString().trim { it <= ' ' }
                val phone = phoneET!!.text.toString().trim { it <= ' ' }
                val address = addressET!!.text.toString().trim { it <= ' ' }
                val zipcode = zipcodeET!!.text.toString().trim { it <= ' ' }
                if(user_pic_file!=null) {
                    presenter!!.checkValidation(firstName, lastName, phone, address, zipcode, bday.toString(), user_pic_file, proof_file, updated_on!!, store!!.getString(Constants.REMEMBER_TOKEN)!!)
                } else {
                    showToast("You cannot leave user image empty",false)
                }
            }
            R.id.browseBT -> {
                places = false
                user = false
                if (checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 200, this)) {
                    ImageUtils.ImageSelect.Builder(this, this, 200).start()
                }
            }
            R.id.iv_back -> finish()
            R.id.dobET -> datePicker()
            R.id.addressET -> {
                places = true
                try {
                    val intent1 = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this)
                    startActivityForResult(intent1, 1)
                } catch (e: GooglePlayServicesNotAvailableException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR,2000)
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dobET!!.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    bday = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                }, year, month, day)
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    override fun permGranted(resultCode: Int) {
        if (resultCode == 200)
            ImageUtils.ImageSelect.Builder(this, this, 200).crop().start()
    }

    override fun permDenied(resultCode: Int) {

    }

    override fun onImageSelected(imagePath: String?, resultCode: Int) {
    }

    var thumbnail:Bitmap?= null
    var id_proof_file:Bitmap?= null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("CreateProfile", "onActivityResult")
        if(places) {
            val place = PlaceAutocomplete.getPlace(this, data)
            if(place!=null) {
                addressET!!.setText(place.address!!.toString())
            }
        } else {
            if (user) {
                if (requestCode == 2) {
                    if (data != null) {
                        thumbnail = data!!.extras!!.get("data") as Bitmap
                        // creating file from camera
                        user_pic_file = ImageUtils.bitmapToFile(thumbnail!!, this)
                    }
                } else {
                    if (data != null) {
                        val inputUri = data!!.data
                        val imagePath = ImageUtils.getRealPath(this, inputUri)
                        thumbnail = ImageUtils.imageCompress(imagePath!!, 4000f, 3000f)

                        // creating file from gallery
                        var selectedImage = data!!.data
                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null)
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val filePath = cursor.getString(columnIndex)
                            user_pic_file = File(filePath)
                        }
                        cursor.close()
                    }
                }
            } else {
                if (requestCode == 2) {
                    if (data != null) {
                        thumbnail = data!!.extras!!.get("data") as Bitmap
                        // creating file from camera
                        proof_file = ImageUtils.bitmapToFile(thumbnail!!, this)
                    }
                } else {
                    if (data != null) {
                        val inputUri = data!!.data
                        val imagePath = ImageUtils.getRealPath(this, inputUri)
                        thumbnail = ImageUtils.imageCompress(imagePath!!, 4000f, 3000f)

                        // creating file from gallery
                        var selectedImage = data!!.data
                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null)
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

        if (resultCode == Activity.RESULT_OK) {
                if (user) {
                    userIV!!.setImageBitmap(thumbnail)
                } else {
                    idproofET!!.setText("Proof.jpg")
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    override fun onSuccess(response: CreateProfileData) {
        store!!.setBoolean(Constants.LOGIN,true)
        showToast(response.message, false)
        startActivity(Intent(this, MainActivity::class.java))
        store!!.setProfileData(response.user_info as ProfileData)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun onError(error: String) {
        showToast(error,false)
    }

    override fun onUnauthorized(error: String) {
        goToLoginActivity()
    }

    override fun onShowProgress() {
       showProgressDialog(this)
    }

    override fun onHideProgress() {
        hideProgressDialog()
    }



}
