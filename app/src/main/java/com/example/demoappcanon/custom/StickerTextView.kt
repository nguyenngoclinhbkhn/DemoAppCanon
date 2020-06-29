package com.example.demoappcanon.custom

import android.R
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi


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
        tv_main?.typeface = font
        context?.resources?.getColor(color)?.let { tv_main?.setTextColor(it) }
    }

    //        tv_main.setTypeface(typeFace);
    override val mainView: View?
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            if (tv_main != null) return tv_main
            tv_main = AutoResizeTextView(context)
            tv_main!!.gravity = Gravity.CENTER
            //        tv_main.setTypeface(typeFace);
            tv_main!!.setMinTextSize(15F)
            tv_main!!.textSize = 1000F
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
        if (tv_main != null) tv_main!!.text = "$text   "
    }

    val text: String?
        get() = if (tv_main != null) tv_main!!.text.toString() else null


    companion object {
        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity: Float =
                context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }
    }
}