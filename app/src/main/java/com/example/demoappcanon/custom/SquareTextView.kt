package com.example.demoappcanon.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.example.demoappcanon.R

class SquareTextView : AppCompatTextView {
    var rect: RectF? = null
    var paint: Paint? = null
    private var bgColor = Color.TRANSPARENT
    var radius = 0f
    var max = -1

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        rect = RectF()
        rect!!.top = 0f
        rect!!.left = rect!!.top
        paint = Paint()
        paint!!.style = Paint.Style.FILL
        paint!!.isAntiAlias = true
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SquareTextView)
        bgColor = ta.getColor(R.styleable.SquareTextView_backgroundColor, Color.TRANSPARENT)
        radius = ta.getDimension(R.styleable.SquareTextView_cornerRadius, DEFAULT_RADIUS_VALUE)
        paint!!.color = bgColor
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(rect!!, radius, radius, paint!!)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        max = Math.max(measuredWidth, measuredHeight)
        rect!!.bottom = max.toFloat()
        rect!!.right = max.toFloat()
        setMeasuredDimension(max, max)
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        this.bgColor = backgroundColor
        paint!!.color = backgroundColor
        invalidate()
        requestLayout()
    }

    fun setCornerRadius(radiusInpx: Int) {
        radius = radiusInpx * context.resources.displayMetrics.density
        invalidate()
        requestLayout()
    }

    companion object {
        private const val DEFAULT_RADIUS_VALUE = 8f
    }
}