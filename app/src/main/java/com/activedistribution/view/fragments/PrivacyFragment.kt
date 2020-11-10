package com.activedistribution.view.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import com.activedistribution.R

class PrivacyFragment : BaseFragment() {

    private var webview: WebView? = null
    private var progressBar: ProgressDialog? = null
    private var iv_back: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       var view = inflater.inflate(R.layout.fragment_privacy, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        webview = view!!.findViewById(R.id.webview)
        iv_back = view!!.findViewById(R.id.iv_back)
        val settings = webview!!.settings
        settings.javaScriptEnabled = true
        webview!!.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        val alertDialog = AlertDialog.Builder(baseActivity).create()
        progressBar = ProgressDialog.show(baseActivity, "", getString(R.string.loading))
        webview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (progressBar!!.isShowing) {
                    progressBar!!.dismiss()
                }
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(baseActivity, resources.getString(R.string.something_went_wrong) + description, Toast.LENGTH_SHORT).show()
                alertDialog.setTitle("Error")
                alertDialog.setMessage(description)
                alertDialog.setButton(resources.getString(R.string.ok), DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })
                alertDialog.show()
            }
        }
        webview!!.loadUrl(baseActivity.store!!.getString("about_url"))
        iv_back!!.setOnClickListener {
            baseActivity.store!!.setBoolean("edit_back",false)
            baseActivity.onBackPressed()
        }
    }
}
