package com.activedistribution.view.customfont

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class TextViewGorgia : android.support.v7.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    //    @SuppressLint("NewApi")
    //    public TextViewRegular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //        super(context, attrs, defStyleAttr, defStyleRes);
    //        init();
    //    }

    private fun init() {
        val tf = Typeface.createFromAsset(context.assets, "Georgia.ttf")
        typeface = tf
    }
}
