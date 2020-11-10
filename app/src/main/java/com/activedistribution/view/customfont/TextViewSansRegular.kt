package com.activedistribution.view.customfont

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class TextViewSansRegular : android.support.v7.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val tf = Typeface.createFromAsset(context.assets, "OpenSans_Regular.ttf")
        typeface = tf
    }
}
