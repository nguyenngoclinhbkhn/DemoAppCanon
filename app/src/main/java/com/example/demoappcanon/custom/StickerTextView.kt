package com.example.demoappcanon.custom

import android.R
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup


class StickerTextView : StickerView {
    private var tv_main: AutoResizeTextView? = null
    private val color = 0
    private val typeFace: Typeface? = null
    private val colo1: Int = android.graphics.Color.BLACK
    private val font: String? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    fun setFontAndColor(context: Context?, font: Typeface?, color: Int) {
        tv_main?.setTypeface(font)
        tv_main?.setTextColor(color)
    }

    //        tv_main.setTypeface(typeFace);
    override val mainView: View?
        get() {
            if (tv_main != null) return tv_main
            tv_main = AutoResizeTextView(context)
            tv_main!!.gravity = Gravity.CENTER
            //        tv_main.setTypeface(typeFace);
            tv_main!!.textSize = 500F
            tv_main!!.setMinTextSize(15F)
            tv_main!!.maxLines = 1
            val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER_VERTICAL
            tv_main!!.isSingleLine = true
            tv_main!!.layoutParams = params
            if (getImageViewFlip() != null) getImageViewFlip()!!.visibility = View.GONE
            return tv_main
        }

    fun setLines(count: Int) {
        if (tv_main != null) {
            tv_main!!.setLines(count)
            tv_main!!.setLineSpacing(1f, 1f)
        }
    }

    fun setText(text: String) {
        if (tv_main != null) tv_main!!.setText("$text   ")
    }

    val text: String?
        get() = if (tv_main != null) tv_main!!.getText().toString() else null



    companion object {
        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity: Float =
                context.getResources().getDisplayMetrics().scaledDensity
            return px / scaledDensity
        }
    }
}