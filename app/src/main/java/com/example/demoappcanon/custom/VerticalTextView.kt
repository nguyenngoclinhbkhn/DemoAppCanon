package com.example.demoappcanon.custom

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.RequiresApi


class VerticalTextView: TextView {
    private var topDown = false
    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes){
        val finalGravity = gravity
        if (Gravity.isVertical(gravity)
            && (gravity and Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((gravity and  Gravity.HORIZONTAL_GRAVITY_MASK)
            or  Gravity.TOP);
            topDown = false;
        } else
        topDown = true;
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState
        canvas?.save()

        if (topDown){
            canvas?.translate(0F, height.toFloat())
            canvas?.rotate(-90F)
        }else{
            canvas?.translate(width.toFloat(), 0F)
            canvas?.rotate(90F)
        }

        canvas?.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
        layout.draw(canvas)
        canvas?.restore()
    }


}