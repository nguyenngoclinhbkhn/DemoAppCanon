package com.example.demoappcanon.custom

import android.R.attr.path
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.example.demoappcanon.Draw
import kotlin.math.abs
import kotlin.math.sqrt


class DrawView : View {
    private var paint: Paint? = null
    private var paint2: Paint?= null
    private var paint3: Paint?= null
    private val colorFinal: Int = Color.RED
    private var mBitmapPaint: Paint? = null
    private var bitmap: Bitmap? = null
    private var count = 0
    private var xFirst = 0f
    private var yFirst = 0f
    private var width = 0F
    private var height = 0F
    private var mPath: Path? = null
    private var mX = 0f
    private var mY = 0f
    private var mCanvas: Canvas? = null
    private var xTest = 0f
    private var yTest = 0f
    private var pointListAfterDraw = arrayListOf<Point>()
    private lateinit var pairSizeBitmapNeedToCut: Pair<Int, Int>
    private lateinit var pairPositionToPutBitmapInFrame: Pair<Int, Int>
    private lateinit var pairCenterPositionXY: Pair<Int, Int>
    private var draw = 0


    // cái này để vẽ line thứ 5
    private lateinit var paint4: Paint
    private lateinit var paint5: Paint
    private lateinit var paintRedShadow : Paint



    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        width = w.toFloat()
        height = h.toFloat()
        mCanvas = Canvas(bitmap!!)
    }

    private fun init() {
        mPath = Path()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint?.color = Color.RED
        paint?.strokeJoin = Paint.Join.ROUND
        paint?.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paint?.style = Paint.Style.STROKE
        paint?.isDither = true
        paint?.strokeWidth = 38F




        this.setOnTouchListener(touchListener)
    }

    fun resetDrawView(){
        mCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//        bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
//        invalidate()
    }




    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap!!, 0F, 0F, mBitmapPaint)
        if (mPath != null) {
            canvas.drawPath(mPath!!, paint!!)
//            canvas.drawPath(mPath!!, paint4)
//            canvas.drawPath(mPath!!, paint5)
//            canvas.drawPath(mPath!!, paintRedShadow)
        }

    }


    private fun touch_start(x: Float, y: Float) {
        mPath?.reset()
        mPath?.moveTo(x, y)
        pointListAfterDraw.add(Point(x.toInt(), y.toInt()))
        mX = x
        mY = y
    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener =
        OnTouchListener { view, motionEvent ->
            val x = motionEvent.x
            val y = motionEvent.y
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    xTest = motionEvent.rawX
                    yTest = motionEvent.rawY
                    xFirst = motionEvent.rawX
                    yFirst = motionEvent.rawY
                    touch_start(x, y)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    touch_move(x, y)
                    val xNew = motionEvent.rawX
                    val yNew = motionEvent.rawY
                    val orgX = xNew - xTest
                    val orgY = yNew - xTest
                    xTest = xNew
                    xTest = yNew
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    touch_up()
                    onDrawViewListener.onDrawViewTouchUp()
                    resetDrawView()
                    invalidate()
                }
            }
            true
        }

    private fun touch_move(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath?.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            pointListAfterDraw.add(Point(mX.toInt(), mY.toInt()))
            mX = x
            mY = y
        }
    }

    private fun touch_up() {
        mPath?.lineTo(mX, mY)
        pointListAfterDraw.add(Point(mX.toInt(), mY.toInt()))
        when(draw){
            Draw.DRAW_1.value -> {
                mCanvas?.drawPath(mPath!!, paint!!)
            }
            Draw.DRAW_2.value -> {
                mCanvas?.drawPath(mPath!!, paint2!!)
            }
            Draw.DRAW_3.value -> {
                mCanvas?.drawPath(mPath!!, paint!!)
                mCanvas?.drawPath(mPath!!, paint3!!)
            }
            Draw.DRAW_4.value -> {
                mCanvas?.drawPath(mPath!!, paintBlur!!)
                mCanvas?.drawPath(mPath!!, paintBlur2!!)
                mCanvas?.drawPath(mPath!!, paint4Inside!!)
            }
            Draw.DRAW_5.value -> {
                mCanvas?.drawPath(mPath!!, paint!!)
                mCanvas?.drawPath(mPath!!, paint4)
                mCanvas?.drawPath(mPath!!, paint5)
                mCanvas?.drawPath(mPath!!, paintRedShadow)
            }
        }
        pairSizeBitmapNeedToCut = findWidthHeightBitmapNeedToCut(pointListAfterDraw)
        pointListAfterDraw.clear()
        mPath?.reset()
    }





    companion object {
        private const val TOUCH = 4
        private const val DISTANCE_FINGER = 50
        private const val TOUCH_TOLERANCE = 4f
        fun convertDpToPixel(dp: Float, context: Context): Int {
            val resources: Resources = context.resources
            val metrics: DisplayMetrics = resources.displayMetrics
            val px = dp * (metrics.densityDpi / 160f)
            return px.toInt()
        }
    }


    fun setDraw1(){
        draw = Draw.DRAW_1.value
        paint?.color = Color.RED

    }
    fun setDraw2(){
        draw = Draw.DRAW_2.value
        paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint2?.color = Color.RED
        paint2?.strokeJoin = Paint.Join.ROUND
        paint2?.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paint2?.style = Paint.Style.STROKE
        paint2?.isDither = true
        paint2?.strokeWidth = 38F
        paint2?.alpha = 50
    }

    fun setDraw3(){
        draw = Draw.DRAW_3.value
        paint3 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint3?.color = Color.WHITE
        paint3?.strokeJoin = Paint.Join.ROUND
        paint3?.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paint3?.style = Paint.Style.STROKE
        paint3?.isDither = true
        paint3?.strokeWidth = 33F
    }

    private var paintBlur: Paint ?= null
    private var paint4Inside: Paint ?= null
    private var paintBlur2: Paint ?= null
    fun setDraw4(){
        draw = Draw.DRAW_4.value
        paintBlur = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBlur?.color = Color.RED
        paintBlur?.strokeJoin = Paint.Join.ROUND
        paintBlur?.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paintBlur?.style = Paint.Style.STROKE
        paintBlur?.isDither = true
        paintBlur?.strokeWidth = 38F
//        paintBlur?.setShadowLayer(12F, 4F, 4F, Color.RED)
        paintBlur?.maskFilter = BlurMaskFilter(20F, BlurMaskFilter.Blur.OUTER)
        paintBlur2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBlur2?.color = Color.RED
        paintBlur2?.strokeJoin = Paint.Join.ROUND
        paintBlur2?.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paintBlur2?.style = Paint.Style.STROKE
        paintBlur2?.isDither = true
        paintBlur2?.strokeWidth = 39F
        paintBlur2?.maskFilter = BlurMaskFilter(20F, BlurMaskFilter.Blur.OUTER)



        paint4Inside = Paint(Paint.ANTI_ALIAS_FLAG)
        paint4Inside?.color = Color.WHITE
        paint4Inside?.strokeJoin = Paint.Join.ROUND
        paint4Inside?.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paint4Inside?.style = Paint.Style.STROKE
        paint4Inside?.isDither = true
        paint4Inside?.strokeWidth = 39F
//        paint4Inside?.setShadowLayer(12F, -12F, -12F, Color.RED)

    }

    fun setDraw5(){
        draw = Draw.DRAW_5.value
        paint?.color = Color.BLACK
        paint4 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint4.color = Color.WHITE
        paint4.strokeJoin = Paint.Join.ROUND // set the join to round you want
        paint4.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paint4.style = Paint.Style.STROKE
        paint4.isDither = true
        paint4.strokeWidth = 32F


        paint5 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint5.color = Color.RED
        paint5.strokeJoin = Paint.Join.ROUND // set the join to round you want
        paint5.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paint5.style = Paint.Style.STROKE
        paint5.isDither = true
        paint5.strokeWidth = 28F

        paintRedShadow = Paint(Paint.ANTI_ALIAS_FLAG)
        paintRedShadow.color = Color.RED
        paintRedShadow.strokeJoin = Paint.Join.ROUND // set the join to round you want
        paintRedShadow.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        paintRedShadow.style = Paint.Style.STROKE
        paintRedShadow.isDither = true
        paintRedShadow.strokeWidth = 22F
        paintRedShadow.setShadowLayer(3F, 3F, 3F, Color.WHITE)
    }

    fun getBitmapNeedToCut(): Bitmap?{
        val bitmapSum = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
//        bitmapSum?.recycle()
        return Bitmap.createBitmap(bitmapSum!!, pairPositionToPutBitmapInFrame.first,
        pairPositionToPutBitmapInFrame.second,
        pairSizeBitmapNeedToCut.first,
        pairSizeBitmapNeedToCut.second)
    }

    private fun findWidthHeightBitmapNeedToCut(list: ArrayList<Point>): Pair<Int, Int>{
        val listX = arrayListOf<Int>()
        val listY = arrayListOf<Int>()
        val value = StickerView.convertDpToPixel(30F, context)
        list.forEach {
            listX.add(it.x)
            listY.add(it.y)
        }
        listX.sortBy { it }
        listY.sortBy { it }
        val xMin = if (listX.first().minus(value) < 0){
            0
        }else{
            listX.first().minus(value)
        }
        val xMax = if (listX.last().plus(value) > bitmap!!.width){
            bitmap!!.width
        }else{
            listX.last().plus(value)
        }
        val yMin = if(listY.first().minus(value) < 0){
            0
        }else{
            listY.first().minus(value)
        }
        val yMax = if(listY.last().plus(value) > bitmap!!.height){
            bitmap!!.height
        }else{
            listY.last().plus(value)
        }
        pairPositionToPutBitmapInFrame = Pair(xMin, yMin)
        return Pair(abs(xMax - xMin), (yMax - yMin))
    }

    fun getPairSizeBitmapNeedToCut(): Pair<Int, Int>{
        return pairSizeBitmapNeedToCut
    }

    fun getPairCenterPositionXY(){

    }

    fun getPairPositionToPutBitmapInFrame(): Pair<Int, Int>{
        return pairPositionToPutBitmapInFrame
    }

    fun setOnDrawViewListener(onDrawViewListener: OnDrawViewListener){
        this.onDrawViewListener = onDrawViewListener
    }
    private lateinit var onDrawViewListener: OnDrawViewListener
    interface OnDrawViewListener{
        fun onDrawViewTouchUp()
    }

}