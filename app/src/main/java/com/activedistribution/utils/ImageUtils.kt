package com.activedistribution.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast
import com.activedistribution.R
import com.soundcloud.android.crop.Crop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageUtils private constructor(builder: ImageSelect.Builder) {
    private val onlyCamera: Boolean
    private val onlyGallery: Boolean
    private val doCrop: Boolean

    init {
        activity = builder.activity
        reqCode = builder.reqCode
        imageSelectCallback = builder.imageSelectCallback
        this.onlyCamera = builder.onlyCamera
        this.onlyGallery = builder.onlyGallery
        this.doCrop = builder.doCrop
        width = builder.width
        height = builder.height
    }

    class ImageSelect {

        class Builder(val activity: Activity, internal val imageSelectCallback: ImageSelectCallback, internal val reqCode: Int) {
            internal var onlyCamera: Boolean = false
            internal var onlyGallery: Boolean = false
            internal var doCrop: Boolean = false
            internal var width: Int = 0
            internal var height: Int = 0

            fun onlyCamera(onlyCamera: Boolean): Builder {
                this.onlyCamera = onlyCamera
                return this
            }

            fun onlyGallery(onlyGallery: Boolean): Builder {
                this.onlyGallery = onlyGallery
                return this
            }

            fun crop(): Builder {
                this.doCrop = true
                return this
            }

            fun aspectRatio(width: Int, height: Int): Builder {
                this.width = width
                this.height = height
                this.doCrop = true
                return this
            }

            fun start() {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Write External storage Permission not specified", Toast.LENGTH_LONG).show()
                    return
                }
                imageUtils = ImageUtils(this)
                if (imageUtils!!.onlyCamera) {
                    captureCameraImage()
                } else if (imageUtils!!.onlyGallery) {
                    selectGalleryImage()
                } else {
                    selectImageDialog()
                }
            }
        }
    }

    interface ImageSelectCallback {
        fun onImageSelected(imagePath: String?, resultCode: Int)
    }

    companion object {
        private val outputUri: Uri? = null
        private var activity: Activity? = null
        private var imageUtils: ImageUtils? = null
        private var imageSelectCallback: ImageSelectCallback? = null
        private var reqCode: Int?=null
        private var width: Int?=null
        private var height: Int?=null
        private var inputUri: Uri? = null

        private fun selectImageDialog() {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(activity!!.getString(R.string.choose_image_source))
            builder.setItems(arrayOf<CharSequence>("Gallery", "Camera")) { dialog, which ->
                when (which) {
                    0 -> selectGalleryImage()
                    1 -> captureCameraImage()
                }
            }
            builder.show()
        }

        private fun captureCameraImage() {
            val takePictureIntent: Intent
            val storageDir: File
            val imageFile: File

            takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            imageFile = File(storageDir, System.currentTimeMillis().toString() + ".jpg")


            if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
                inputUri = FileProvider.getUriForFile(activity!!, activity!!.getString(R.string.file_provider_authority_), imageFile)
                val packageManager = activity!!.packageManager
                val activities = packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolvedIntentInfo in activities) {
                    val packageName = resolvedIntentInfo.activityInfo.packageName
                    activity!!.grantUriPermission(packageName, inputUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                activity!!.startActivityForResult(takePictureIntent, 2)
            }
        }

        private fun selectGalleryImage() {
            try {
                val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                intent1.type = "image/*"
                activity!!.startActivityForResult(intent1, 1)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(activity, "No app found to perform this action", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(activity, "No app found to perform this action", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

        }

        fun activityResult(requestCode: Int?, resultCode: Int?, data: Intent?) {
            Log.e("CreateProfile", "onActivityResult")
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                inputUri = data!!.data
                reqCode?.let { sendBackImagePath(inputUri, it) }
            } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                reqCode?.let { sendBackImagePath(inputUri, it) }
            } else if (resultCode == Activity.RESULT_OK) {
                reqCode?.let { sendBackImagePath(outputUri, it) }
            } else if (resultCode == Crop.RESULT_ERROR) {
                Toast.makeText(activity, "Write External storage Permission not specified", Toast.LENGTH_SHORT).show()
            }
        }


        private fun sendBackImagePath(inputUri: Uri?, reqCode: Int) {
            val path = getRealPath(activity, inputUri)
            imageSelectCallback!!.onImageSelected(path, reqCode)
        }

        private fun Crop() {

            if (width != 0) {
                width?.let { height?.let { it1 -> Crop.of(inputUri, outputUri).withAspect(it, it1).start(activity) } }
            } else
                Crop.of(inputUri, outputUri).asSquare().start(activity)
        }

        fun bitmapToFile(bitmap: Bitmap, activity: Activity): File {
            val f = File(activity.cacheDir, System.currentTimeMillis().toString() + ".jpg")
            try {
                f.createNewFile()
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
                val bitmapdata = bos.toByteArray()

                val fos = FileOutputStream(f)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()
            } catch (ioexception: IOException) {
                ioexception.printStackTrace()
            }

            return f
        }

        fun getRealPath(activity: Context?, uri: Uri?): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(activity, uri)) {
                if (isExternalStorageDocument(uri!!)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                    return getDataColumn(activity!!, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(activity!!, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri))
                    return uri.lastPathSegment
                else if (isCachePhotoUri(uri))
                    return activity!!.cacheDir.toString() + File.separator + uri.lastPathSegment
                else if (isExternalStoragePath(uri))
                    return Environment.getExternalStorageDirectory().toString() + File.separator + uri.path.split("/external_files/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                else {
                    if (getDataColumn(activity!!, uri, null, null) != null)
                        return getDataColumn(activity, uri, null, null)
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }

            return uri.path
        }

        private fun isCachePhotoUri(uri: Uri): Boolean {
            return uri.path.contains("cache/")
        }

        private fun isExternalStoragePath(uri: Uri): Boolean {
            return uri.path.contains("external_files/")
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        private fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                if (cursor != null)
                    cursor.close()
            }
            return null
        }

        @JvmOverloads
        fun imageCompress(picturePath: String, maxHeight: Float = 816.0f, maxWidth: Float = 612.0f): Bitmap {

            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            var bmp = BitmapFactory.decodeFile(picturePath, options)

            var actualHeight = options.outHeight
            var actualWidth = options.outWidth
            var imgRatio = (actualWidth / actualHeight).toFloat()
            val maxRatio = maxWidth / maxHeight

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            } else {
                var bitmap: Bitmap
                bitmap = BitmapFactory.decodeFile(picturePath)
                bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true)
                //            return bitmap;
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            options.inJustDecodeBounds = false

            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            try {
                bmp = BitmapFactory.decodeFile(picturePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

            val canvas = Canvas(scaledBitmap!!)
            canvas.matrix = scaleMatrix
            canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
            bmp.recycle()
            val exif: ExifInterface
            try {
                exif = ExifInterface(picturePath)

                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.width, scaledBitmap.height, matrix,
                        true)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return scaledBitmap!!
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }

            return inSampleSize
        }

        fun blur(context: Activity, image: Bitmap): Bitmap? {

            val BITMAP_SCALE = 0.4f
            val BLUR_RADIUS = 5f

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val width = Math.round(image.width * BITMAP_SCALE)
                val height = Math.round(image.height * BITMAP_SCALE)

                val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
                val outputBitmap = Bitmap.createBitmap(inputBitmap)

                val rs = RenderScript.create(context)
                var theIntrinsic: ScriptIntrinsicBlur? = null
                theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

                val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
                val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
                theIntrinsic!!.setRadius(BLUR_RADIUS)
                theIntrinsic.setInput(tmpIn)
                theIntrinsic.forEach(tmpOut)
                tmpOut.copyTo(outputBitmap)
                return outputBitmap
            }
            return null
        }
    }

}
