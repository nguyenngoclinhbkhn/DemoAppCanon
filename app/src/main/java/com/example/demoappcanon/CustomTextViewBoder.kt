package com.example.demoappcanon

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView


class CustomTextViewBorder : TextView {
    private var strokeWidth = 0f
    private var strokeColor: Int? = null
    private var strokeJoin: Paint.Join? = null
    private var strokeMiter: Float? = 0f

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CustomTextViewBorder)
            if (a.hasValue(R.styleable.CustomTextViewBorder_strokeColor)) {
                val strokeWidth =
                    a.getDimensionPixelSize(R.styleable.CustomTextViewBorder_strokeWidth, 1).toFloat()
                val strokeColor =
                    a.getColor(R.styleable.CustomTextViewBorder_strokeColor, -0x1000000)
                val strokeMiter =
                    a.getDimensionPixelSize(R.styleable.CustomTextViewBorder_strokeMiter, 10).toFloat()
                var strokeJoin: Paint.Join? = null
                when (a.getInt(R.styleable.CustomTextViewBorder_strokeJoinStyle, 0)) {
                    0 -> strokeJoin = Paint.Join.MITER
                    1 -> strokeJoin = Paint.Join.BEVEL
                    2 -> strokeJoin = Paint.Join.ROUND
                }
                setStroke(strokeWidth, strokeColor, strokeJoin, strokeMiter)
            }
        }
    }

    fun setStroke(width: Float, color: Int, join: Paint.Join? = null, miter: Float?= null ) {
        strokeWidth = 3F
        strokeColor = Color.WHITE
        strokeJoin = join
        strokeMiter = miter
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val restoreColor = this.currentTextColor
        if (strokeColor != null) {
            val paint = this.paint
            paint.style = Paint.Style.STROKE
            strokeJoin?.let {
                paint.strokeJoin = it
            }
            strokeMiter?.let {
                paint.strokeMiter = it
            }
            this.setTextColor(strokeColor!!)
            paint.strokeWidth = strokeWidth
            super.onDraw(canvas)
            paint.style = Paint.Style.FILL
            this.setTextColor(restoreColor)
        }
    }
}