package com.example.demoappcanon.custom

import android.annotation.SuppressLint
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.demoappcanon.R


abstract class StickerView : FrameLayout {
    private lateinit var imageViewBorder: BorderView
    private lateinit var imageViewScale: ImageView
    private lateinit var imageViewDelete: ImageView
    private lateinit var imageViewFlip: ImageView
    private lateinit var imageViewDone: ImageView
    private val TAG = "TAG"
    private var mDist = 0f
    var isEdit = false

    // For scalling
    private var this_orgX = -1f
    private var this_orgY = -1f
    private var scale_orgX = -1f
    private var scale_orgY = -1f
    private var scale_orgWidth = -1.0
    private var scale_orgHeight = -1.0

    // For rotating
    private var rotate_orgX = -1f
    private var rotate_orgY = -1f
    private var rotate_newX = -1f
    private var rotate_newY = -1f

    // For moving
    private var move_orgX = -1f
    private var move_orgY = -1f
    private var centerX = 0.0
    private var centerY = 0.0

    private lateinit var stickerListener: OnStickerListener

    interface OnStickerListener {
        fun onStickerChoose(sticker: StickerView)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context )
    }

    fun setStickerListener(stickerListener: OnStickerListener){
        this.stickerListener = stickerListener
    }

    private fun init(context: Context) {
        imageViewBorder = BorderView(context)
        imageViewScale = ImageView(context)
        imageViewDelete = ImageView(context)
        imageViewFlip = ImageView(context)
        imageViewDone = ImageView(context)

//        this.imageViewDone.setImageResource(R.drawable.edit);
        imageViewScale.setImageResource(R.drawable.ic_scale)
        imageViewDelete.setImageResource(R.drawable.ic_launcher_background)
        imageViewDelete.visibility = View.GONE
        imageViewFlip.setImageResource(R.drawable.ic_flip)
        this.tag = "DraggableViewGroup"
        imageViewBorder.setTag("iv_border")
        imageViewScale.setTag("iv_scale")
        imageViewDelete.setTag("iv_delete")
        imageViewFlip.setTag("iv_flip")
        val margin = convertDpToPixel(
            BUTTON_SIZE_DP.toFloat(),
            getContext()
        ) / 2
        val size = convertDpToPixel(
            SELF_SIZE_DP.toFloat(),
            getContext()
        )
        val this_params = LayoutParams(
            size,
            size
        )
        this_params.gravity = Gravity.CENTER
        val iv_main_params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        iv_main_params.setMargins(margin, margin, margin, margin)
        val iv_border_params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        iv_border_params.setMargins(margin, margin, margin, margin)
        val iv_scale_params = LayoutParams(
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            ),
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            )
        )
        iv_scale_params.gravity = Gravity.BOTTOM or Gravity.RIGHT
        val iv_delete_params = LayoutParams(
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            ),
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            )
        )
        iv_delete_params.gravity = Gravity.TOP or Gravity.RIGHT
        val iv_flip_params = LayoutParams(
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            ),
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            )
        )
        iv_flip_params.gravity = Gravity.BOTTOM or Gravity.LEFT
        val imageViewDone_params = LayoutParams(
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            ),
            convertDpToPixel(
                BUTTON_SIZE_DP.toFloat(),
                getContext()
            )
        )
        imageViewDone_params.gravity = Gravity.TOP or Gravity.LEFT
        this.layoutParams = this_params
        this.addView(mainView, iv_main_params)
        this.addView(imageViewBorder, iv_border_params)
        this.addView(imageViewScale, iv_scale_params)
        this.addView(imageViewDelete, iv_delete_params)
        this.addView(imageViewFlip, iv_flip_params)
        this.addView(imageViewDone, imageViewDone_params)
        setOnTouchListener(mTouchListener)
        imageViewScale.setOnTouchListener(mTouchListener)
        imageViewDelete.setOnClickListener(OnClickListener {
            if (this@StickerView.parent != null) {
                val myCanvas = this@StickerView.parent as ViewGroup
                myCanvas.removeView(this@StickerView)
            }
        })
        imageViewFlip.setOnClickListener(OnClickListener {
            Log.v(TAG, "flip the view")
            val mainView: View = mainView as View
            mainView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.flip))
            mainView.rotationY = if (mainView.rotationY == -180f) 0f else -180f
            mainView.invalidate()
            requestLayout()
        })
        imageViewDone.setOnClickListener(OnClickListener { //                setEdit(true);
            Toast.makeText(getContext(), "set true ok", Toast.LENGTH_SHORT).show()
        })
    }

    interface Sendata {
        fun sendData(stickerView: StickerView?)
    }


    val isFlip: Boolean
        get() = mainView?.rotationY === -180f

    protected abstract val mainView: View?
    @SuppressLint("ClickableViewAccessibility")
    private val mTouchListener = OnTouchListener { view, event ->
        if (view.tag.equals("DraggableViewGroup")) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.v(TAG, "sticker view action down")
                    move_orgX = event.rawX
                    move_orgY = event.rawY
                    stickerListener.onStickerChoose(this)
                }
                MotionEvent.ACTION_MOVE -> {
                    val offsetX = event.rawX - move_orgX
                    val offsetY = event.rawY - move_orgY
                    val coordinateX1 = this@StickerView.x + offsetX
                    val coordinateY1 = this@StickerView.y + offsetY
                    val coordinateX: Float =
                        (this@StickerView.x + offsetX).toFloat()
                    val coordinateY: Float =
                        (this@StickerView.y + offsetY).toFloat()
                    val coordinateXLeft = (this@StickerView.left + offsetX).toInt()
                    val coordinateYTop = (this@StickerView.top + offsetY).toInt()

//                        StickerView.this.setLeft(coordinateXLeft);
//                        StickerView.this.setTop(coordinateYTop);
                    this@StickerView.x = coordinateX
                    this@StickerView.y = coordinateY
                    //                        StickerView.this.setLeft((int) (StickerView.this.getX() + offsetX));
//                        StickerView.this.setTop((int) (StickerView.this.getY() + offsetY));
                    move_orgX = event.rawX
                    move_orgY = event.rawY
                    Log.e("TAG", "x da lam tron $coordinateX")
                    Log.e("TAG", "y da lam tron $coordinateY")
                    Log.e("TAG", "x chua lam tron $coordinateX1")
                    Log.e("TAG", "y chua lam tron $coordinateY1")
                    stickerListener.onStickerChoose(this)
                    invalidate()
                }
                MotionEvent.ACTION_UP ->
//                        Intent intent = new Intent();
//                        intent.putExtra("msg", "DATA ARRIVE");
//                        intent.setAction(EditPhotoActivity.CHOOSE);
//                        getContext().sendBroadcast(intent);
                    Log.v(TAG, "sticker view action up")
            }
        } else if (view.tag.equals("iv_scale")) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.v(TAG, "iv_scale action down")
                    this_orgX = this@StickerView.x
                    this_orgY = this@StickerView.y
                    scale_orgX = event.rawX
                    scale_orgY = event.rawY
                    scale_orgWidth = this@StickerView.layoutParams.width.toDouble()
                    scale_orgHeight = this@StickerView.layoutParams.height.toDouble()
                    rotate_orgX = event.rawX
                    rotate_orgY = event.rawY
                    centerX = this@StickerView.x +
                            (this@StickerView.parent as View).getX() + this@StickerView.width.toDouble() / 2


                    //double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
                    var result = 0
                    val resourceId =
                        resources.getIdentifier("status_bar_height", "dimen", "android")
                    if (resourceId > 0) {
                        result = resources.getDimensionPixelSize(resourceId)
                    }
                    val statusBarHeight = result.toDouble()
                    centerY = this@StickerView.y +
                            (this@StickerView.parent as View).getY() +
                            statusBarHeight + this@StickerView.height.toFloat() / 2
                    stickerListener.onStickerChoose(this)
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.v(TAG, "iv_scale action move")
                    rotate_newX = event.rawX
                    rotate_newY = event.rawY
                    val angle_diff = Math.abs(
                        Math.atan2(
                            event.rawY - scale_orgY.toDouble(),
                            event.rawX - scale_orgX.toDouble()
                        )
                                - Math.atan2(
                            scale_orgY - centerY,
                            scale_orgX - centerX
                        )
                    ) * 180 / Math.PI
                    Log.v(TAG, "angle_diff: $angle_diff")
                    val length1 = getLength(
                        centerX,
                        centerY,
                        scale_orgX.toDouble(),
                        scale_orgY.toDouble()
                    )
                    val length2 = getLength(
                        centerX,
                        centerY,
                        event.rawX.toDouble(),
                        event.rawY.toDouble()
                    )
                    val size = convertDpToPixel(
                        SELF_SIZE_DP.toFloat(),
                        context
                    )
                    if ((length2 > length1
                                && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25))
                    ) {
                        //scale up
                        val offsetX =
                            Math.abs(event.rawX - scale_orgX).toDouble()
                        val offsetY =
                            Math.abs(event.rawY - scale_orgY).toDouble()
                        var offset = Math.max(offsetX, offsetY)
                        offset = Math.round(offset).toDouble()
                        this@StickerView.layoutParams.width += offset.toInt()
                        this@StickerView.layoutParams.height += offset.toInt()
                        onScaling(true)
                        //DraggableViewGroup.this.setX((float) (getX() - offset / 2));
                        //DraggableViewGroup.this.setY((float) (getY() - offset / 2));
                    } else if (((length2 < length1
                                ) && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
                                && (this@StickerView.layoutParams.width > size / 2
                                ) && (this@StickerView.layoutParams.height > size / 2))
                    ) {
                        //scale down
                        val offsetX =
                            Math.abs(event.rawX - scale_orgX).toDouble()
                        val offsetY =
                            Math.abs(event.rawY - scale_orgY).toDouble()
                        var offset = Math.max(offsetX, offsetY)
                        offset = Math.round(offset).toDouble()
                        this@StickerView.layoutParams.width -= offset.toInt()
                        this@StickerView.layoutParams.height -= offset.toInt()
                        onScaling(false)
                    }

                    //rotate
                    val angle = Math.atan2(
                        event.rawY - centerY,
                        event.rawX - centerX
                    ) * 180 / Math.PI
                    Log.v(TAG, "log angle: $angle")

                    //setRotation((float) angle - 45);
                    rotation = angle.toFloat() - 45
                    Log.v(TAG, "getRotation(): $rotation")
                    onRotating()
                    rotate_orgX = rotate_newX
                    rotate_orgY = rotate_newY
                    scale_orgX = event.rawX
                    scale_orgY = event.rawY
                    stickerListener.onStickerChoose(this)
                    postInvalidate()
                    requestLayout()
                }
                MotionEvent.ACTION_UP -> Log.v(TAG, "iv_scale action up")
            }
        }
        true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun getLength(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): Double {
        return Math.sqrt(
            Math.pow(y2 - y1, 2.0) + Math.pow(
                x2 - x1,
                2.0
            )
        )
    }

    private fun getRelativePos(absX: Float, absY: Float): FloatArray {
        Log.v("ken", "getRelativePos getX:" + (this.parent as View).getX())
        Log.v("ken", "getRelativePos getY:" + (this.parent as View).getY())
        val pos = floatArrayOf(
            absX - (this.parent as View).getX(),
            absY - (this.parent as View).getY()
        )
        Log.v(TAG, "getRelativePos absY:$absY")
        Log.v(TAG, "getRelativePos relativeY:" + pos[1])
        return pos
    }

    fun setControlItemsHidden(isHidden: Boolean) {
        if (isHidden) {
            imageViewBorder.setVisibility(View.VISIBLE)
            imageViewScale.setVisibility(View.VISIBLE)
            imageViewDelete.setVisibility(View.VISIBLE)
            imageViewFlip.setVisibility(View.VISIBLE)
            imageViewDone.setVisibility(View.VISIBLE)
        } else {
            imageViewBorder.setVisibility(View.GONE)
            imageViewScale.setVisibility(View.GONE)
            imageViewDelete.setVisibility(View.GONE)
            imageViewFlip.setVisibility(View.GONE)
            imageViewDone.setVisibility(View.GONE)
        }
    }

    private fun handleZoom(event: MotionEvent) {
        val newDist = getFingerSpacing(event)
        this_orgX = this@StickerView.x
        this_orgY = this@StickerView.y
        scale_orgX = event.rawX
        scale_orgY = event.rawY
        scale_orgWidth = this@StickerView.layoutParams.width.toDouble()
        scale_orgHeight = this@StickerView.layoutParams.height.toDouble()
        centerX = this@StickerView.x +
                (this@StickerView.parent as View).getX() + this@StickerView.width.toDouble() / 2


        //double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        val statusBarHeight = result.toDouble()
        centerY = this@StickerView.y +
                (this@StickerView.parent as View).getY() +
                statusBarHeight + this@StickerView.height.toFloat() / 2
        if (newDist > mDist) {
            //zoom in
            val angle_diff = Math.abs(
                Math.atan2(
                    event.rawY - scale_orgY.toDouble(),
                    event.rawX - scale_orgX.toDouble()
                )
                        - Math.atan2(scale_orgY - centerY, scale_orgX - centerX)
            ) * 180 / Math.PI
            Log.v(TAG, "angle_diff: $angle_diff")
            val length1 =
                getLength(centerX, centerY, scale_orgX.toDouble(), scale_orgY.toDouble())
            val length2 =
                getLength(centerX, centerY, event.rawX.toDouble(), event.rawY.toDouble())
            val size = convertDpToPixel(
                SELF_SIZE_DP.toFloat(),
                context
            )
            if (length2 > length1
                && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
            ) {
                //scale up
                val offsetX =
                    Math.abs(event.rawX - scale_orgX).toDouble()
                val offsetY =
                    Math.abs(event.rawY - scale_orgY).toDouble()
                var offset = Math.max(offsetX, offsetY)
                offset = Math.round(offset).toDouble()
                this@StickerView.layoutParams.width += offset.toInt()
                this@StickerView.layoutParams.height += offset.toInt()
                onScaling(true)
                //DraggableViewGroup.this.setX((float) (getX() - offset / 2));
                //DraggableViewGroup.this.setY((float) (getY() - offset / 2));
            }
        } else if (newDist < mDist) {
            //zoom out
            val angle_diff = Math.abs(
                Math.atan2(
                    event.rawY - scale_orgY.toDouble(),
                    event.rawX - scale_orgX.toDouble()
                )
                        - Math.atan2(scale_orgY - centerY, scale_orgX - centerX)
            ) * 180 / Math.PI
            Log.v(TAG, "angle_diff: $angle_diff")
            val length1 =
                getLength(centerX, centerY, scale_orgX.toDouble(), scale_orgY.toDouble())
            val length2 =
                getLength(centerX, centerY, event.rawX.toDouble(), event.rawY.toDouble())
            val size = convertDpToPixel(
                SELF_SIZE_DP.toFloat(),
                context
            )
            if (length2 < length1 && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
                && this@StickerView.layoutParams.width > size / 2 && this@StickerView.layoutParams.height > size / 2
            ) {
                //scale down
                val offsetX =
                    Math.abs(event.rawX - scale_orgX).toDouble()
                val offsetY =
                    Math.abs(event.rawY - scale_orgY).toDouble()
                var offset = Math.max(offsetX, offsetY)
                offset = Math.round(offset).toDouble()
                this@StickerView.layoutParams.width -= offset.toInt()
                this@StickerView.layoutParams.height -= offset.toInt()
                onScaling(false)
            }
        }
        mDist = newDist
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        // ...
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt(x * x + y * y.toDouble()).toFloat()
    }

    protected fun getImageViewFlip(): View? {
        return imageViewFlip
    }

    fun setVisiableBorderAndButton(){
        imageViewBorder.visibility = View.GONE
        imageViewDelete.visibility = View.GONE
        imageViewFlip.visibility = View.GONE
        imageViewDone.visibility = View.GONE
        imageViewScale.visibility = View.GONE
    }

    protected fun onScaling(scaleUp: Boolean) {}
    protected fun onRotating() {}
    private inner class BorderView : View {
        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
        constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
            context,
            attrs,
            defStyle
        ) {
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            // Draw sticker border
            val params =
                layoutParams as LayoutParams
            Log.v(TAG, "params.leftMargin: " + params.leftMargin)
            val border = Rect()
            border.left = 10
            border.top = 10
            border.right = 10
            border.bottom = 10
            val borderPaint = Paint()
            borderPaint.strokeWidth = 3F
            borderPaint.color = Color.BLACK
            borderPaint.style = Paint.Style.STROKE
            canvas.drawRect(border, borderPaint)
        }
    }

    companion object {
        private const val BUTTON_SIZE_DP = 30
        private const val SELF_SIZE_DP = 100
        private fun convertDpToPixel(dp: Float, context: Context): Int {
            val resources: Resources = context.resources
            val metrics: DisplayMetrics = resources.displayMetrics
            val px = dp * (metrics.densityDpi / 160f)
            return px.toInt()
        }
    }
}