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
        paint?.color = Color.BLACK
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

            }
            Draw.DRAW_2.value -> {

            }
            Draw.DRAW_3.value -> {

            }
            Draw.DRAW_4.value -> {

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

    }
    fun setDraw2(){
        draw = Draw.DRAW_2.value
        paint?.color = colorFinal
        paint?.alpha = 50
    }

    fun setDraw3(){
        draw = Draw.DRAW_3.value
    }

    fun setDraw4(){
        draw = Draw.DRAW_4.value
        paint?.setShadowLayer(2F, -8F, 8F, Color.GREEN )
//        paint?.maskFilter = BlurMaskFilter(12F, BlurMaskFilter.Blur.NORMAL)

    }

    fun setDraw5(){
        draw = Draw.DRAW_5.value
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
        list.forEach {
            listX.add(it.x)
            listY.add(it.y)
        }
        listX.sortBy { it }
        listY.sortBy { it }
        val xMin = if (listX.first().minus(30) < 0){
            0
        }else{
            listX.first().minus(30)
        }
        val xMax = if (listX.last().plus(30) > bitmap!!.width){
            bitmap!!.width
        }else{
            listX.last().plus(30)
        }
        val yMin = if(listY.first().minus(30) < 0){
            0
        }else{
            listY.first().minus(30)
        }
        val yMax = if(listY.last().plus(30) > bitmap!!.height){
            bitmap!!.height
        }else{
            listY.last().plus(30)
        }
        pairPositionToPutBitmapInFrame = Pair(xMin, yMin)

//        pairCenterPositionXY = Pair(xMin + sqrt((xMin - xMax) * (xMin - xMax) + (yMin - yMax) * (yMin - yMax)),
//        yMin)

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