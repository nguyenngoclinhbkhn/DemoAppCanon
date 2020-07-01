package com.example.demoappcanon.custom

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.RectF
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.SparseIntArray
import android.util.TypedValue
import android.widget.TextView


class AutoResizeTextView : TextView {
    private interface SizeTester {
        fun onTestSize(suggestedSize: Int, availableSpace: RectF?): Int
    }

    private val mTextRect = RectF()
    private var mAvailableSpaceRect: RectF? = null
    private var mTextCachedSizes: SparseIntArray? = null
    private var mPaint: TextPaint? = null
    private var mMaxTextSize = 0f
    private var mSpacingMult = 1.0f
    private var mSpacingAdd = 0.0f
    private var mMinTextSize = 20f
    private var mWidthLimit = 0
    private var mMaxLines = 0
    private var mEnableSizeCache = true
    private var mInitiallized = false

    constructor(context: Context?) : super(context) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initialize()
    }

    private fun initialize() {
        mPaint = TextPaint(paint)
        mMaxTextSize = textSize
        mAvailableSpaceRect = RectF()
        mTextCachedSizes = SparseIntArray()
        if (mMaxLines == 0) {
            // no value was assigned during construction
            mMaxLines = NO_LINE_LIMIT
        }
        mInitiallized = true
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, type)
        adjustTextSize(text.toString())
    }

    override fun setTextSize(size: Float) {
        mMaxTextSize = size
        mTextCachedSizes!!.clear()
        adjustTextSize(text.toString())
    }

    override fun setMaxLines(maxlines: Int) {
        super.setMaxLines(maxlines)
        mMaxLines = maxlines
        reAdjust()
    }

    override fun getMaxLines(): Int {
        return mMaxLines
    }

    override fun setSingleLine() {
        super.setSingleLine()
        mMaxLines = 1
        reAdjust()
    }

    override fun setSingleLine(singleLine: Boolean) {
        super.setSingleLine(singleLine)
        mMaxLines = if (singleLine) {
            1
        } else {
            NO_LINE_LIMIT
        }
        reAdjust()
    }

    override fun setLines(lines: Int) {
        super.setLines(lines)
        mMaxLines = lines
        reAdjust()
    }

    override fun setTextSize(unit: Int, size: Float) {
        val c: Context? = context
        val r: Resources
        r = if (c == null) Resources.getSystem() else c.getResources()
        mMaxTextSize = TypedValue.applyDimension(
            unit, size,
            r.getDisplayMetrics()
        )
        mTextCachedSizes!!.clear()
        adjustTextSize(text.toString())
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        mSpacingMult = mult
        mSpacingAdd = add
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    fun setMinTextSize(minTextSize: Float) {
        mMinTextSize = minTextSize
        reAdjust()
    }

    private fun reAdjust() {
        adjustTextSize(text.toString())
    }

    private fun adjustTextSize(string: String) {
        if (!mInitiallized) {
            return
        }
        val startSize = mMinTextSize.toInt()
        val heightLimit = (measuredHeight )
        mWidthLimit = (measuredWidth )
        mAvailableSpaceRect!!.right = mWidthLimit.toFloat()
        mAvailableSpaceRect!!.bottom = heightLimit.toFloat()
        super.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            efficientTextSizeSearch(
                startSize, mMaxTextSize.toInt(),
                mSizeTester, mAvailableSpaceRect
            ).toFloat()
        )
    }

    private val mSizeTester: SizeTester =
        object : SizeTester {

            override fun onTestSize(suggestedSize: Int, availableSpace: RectF?): Int {
                mPaint!!.textSize = suggestedSize.toFloat()
                val text = text.toString()
                val singleline = maxLines == 1
                if (singleline) {
                    mTextRect.bottom = mPaint!!.fontSpacing
                    mTextRect.right = mPaint!!.measureText(text)
                } else {
                    val layout = StaticLayout(
                        text, mPaint,
                        mWidthLimit, Layout.Alignment.ALIGN_NORMAL, mSpacingMult,
                        mSpacingAdd, true
                    )
                    // return early if we have more lines
                    if (maxLines != NO_LINE_LIMIT
                        && layout.lineCount > maxLines
                    ) {
                        return 1
                    }
                    mTextRect.bottom = layout.height.toFloat()
                    var maxWidth = -1
                    for (i in 0 until layout.lineCount) {
                        if (maxWidth < layout.getLineWidth(i)) {
                            maxWidth = layout.getLineWidth(i).toInt()
                        }
                    }
                    mTextRect.right = maxWidth.toFloat()
                }
                mTextRect.offsetTo(0f, 0f)
                return if (availableSpace!!.contains(mTextRect)) {
                    // may be too small, don't worry we will find the best match
                    -1
                } else {
                    // too big
                    1
                }
            }
        }

    /**
     * Enables or disables size caching, enabling it will improve performance
     * where you are animating a value inside TextView. This stores the font
     * size against getText().length() Be careful though while enabling it as 0
     * takes more space than 1 on some fonts and so on.
     *
     * @param enable
     * enable font size caching
     */
    fun enableSizeCache(enable: Boolean) {
        mEnableSizeCache = enable
        mTextCachedSizes!!.clear()
        adjustTextSize(text.toString())
    }

    private fun efficientTextSizeSearch(
        start: Int, end: Int,
        sizeTester: SizeTester, availableSpace: RectF?
    ): Int {
        if (!mEnableSizeCache) {
            return binarySearch(start, end, sizeTester, availableSpace)
        }
        val text = text.toString()
        val key = text?.length ?: 0
        var size = mTextCachedSizes!![key]
        if (size != 0) {
            return size
        }
        size = binarySearch(start, end, sizeTester, availableSpace)
        mTextCachedSizes!!.put(key, size)
        return size
    }

    override fun onTextChanged(
        text: CharSequence, start: Int,
        before: Int, after: Int
    ) {
        super.onTextChanged(text, start, before, after)
        reAdjust()
    }

    override fun onSizeChanged(
        width: Int, height: Int, oldwidth: Int,
        oldheight: Int
    ) {
        mTextCachedSizes!!.clear()
        super.onSizeChanged(width, height, oldwidth, oldheight)
        if (width != oldwidth || height != oldheight) {
            reAdjust()
        }
    }

    companion object {
        private const val NO_LINE_LIMIT = -1
        private fun binarySearch(
            start: Int, end: Int, sizeTester: SizeTester,
            availableSpace: RectF?
        ): Int {
            var lastBest = start
            var lo = start
            var hi = end - 1
            var mid = 0
            while (lo <= hi) {
                mid = lo + hi ushr 1
                val midValCmp = sizeTester.onTestSize(mid, availableSpace)
                if (midValCmp < 0) {
                    lastBest = lo
                    lo = mid + 1
                } else if (midValCmp > 0) {
                    hi = mid - 1
                    lastBest = hi
                } else {
                    return mid
                }
            }
            // make sure to return last best
            // this is what should always be returned
            return lastBest
        }
    }
}