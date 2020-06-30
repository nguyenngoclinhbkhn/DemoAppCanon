package com.example.demoappcanon

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoappcanon.adapter.AdapterButtonDraw
import com.example.demoappcanon.adapter.AdapterStickerOnImage
import com.example.demoappcanon.custom.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, DrawView.OnDrawViewListener,
    StickerView.OnStickerListener, AdapterButtonDraw.OnButtonDrawListener {
    private lateinit var imgPreview: ImageView
    private lateinit var frameRoot: FrameLayout
    private lateinit var stickerRoot: StickerImageView
    private lateinit var drawView: DrawView
    private val widthBitmap = 1600
    private val heightBitmap = 900
    private lateinit var bitmapResize: Bitmap
    private lateinit var bitmapOrigin: Bitmap
    private lateinit var adapterButton: AdapterButtonDraw
    private val REQUEST_CODE = 105
    private var stickerText: StickerTextView? = null
    private var stickerFake: StickerImageView? = null
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var adapterStickerOnImage: AdapterStickerOnImage
    private lateinit var listSticker: ArrayList<StickerModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listSticker = arrayListOf()
        drawView = findViewById(R.id.drawView)
        frameRoot = findViewById(R.id.frameRoot)
        imgPreview = findViewById(R.id.imgPreView)
//        drawView = DrawView(this)
        drawView.setOnClickListener(this)
//        imgPreview = ImageView(this)
        drawView.setOnDrawViewListener(this)
        stickerFake = StickerImageView(this)
        stickerFake?.setStickerListener(this)
        frameRoot.addView(stickerFake)
        stickerFake?.visibility = View.GONE

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x

        drawView.setKind5()


        bitmapOrigin = BitmapFactory.decodeResource(resources, R.drawable.test)


        frameRoot.post {
            val widthFrame = frameRoot.width
            val heightFrame = frameRoot.height
            bitmapResize = Bitmap.createScaledBitmap(
                bitmapOrigin,
                widthFrame,
                heightBitmap * widthFrame / widthBitmap,
                true
            )
            imgPreview.setImageBitmap(bitmapResize)
            val layoutParams = FrameLayout.LayoutParams(
                bitmapResize.width, bitmapResize.height,
                Gravity.CENTER
            )
            imgPreview.layoutParams = layoutParams
            imgPreview.requestLayout()

//            frameRoot.addView(imgPreview, layoutParams)
//            frameRoot.addView(drawView, FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT)
        }

        val btnAddText = findViewById<Button>(R.id.btnAddText).setOnClickListener {
            startActivityForResult(Intent(this, AddTextActivity::class.java), REQUEST_CODE)
        }

        stickerText = StickerTextView(this)
        stickerText?.setStickerListener(this)

        if (drawView.parent != null) {
            (drawView.parent as ViewGroup).removeView(drawView)
        }
        frameRoot.addView(drawView, FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)



        adapterButton = AdapterButtonDraw(this)
        recyclerDraw.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerDraw.adapter = adapterButton
        sticker.setOnClickListener(this)
        save.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sticker -> {
                val stickerImageView = StickerImageView(this)
                stickerImageView.setStickerListener(this)
                stickerImageView.setImageResource(R.drawable.husky)
                frameRoot.addView(stickerImageView)

                val stickerText = StickerTextView(this)
                stickerText.setStickerListener(this)
                stickerText.setText("Xin chao")
                frameRoot.addView(stickerText)
            }

            R.id.save -> {
                Log.e("TAG" , "size sticker focus ${stickerFocus?.width} : ${stickerFocus?.height}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val keyAddText = data?.getStringExtra("KEY_ADD_TEXT")
                    val keyColorText = data?.getIntExtra("KEY_COLOR_TEXT", -1)
                    Log.e("KEY_ADD_TEXT", keyAddText)
                    Log.e("KEY_COLOR_TEXT", keyColorText.toString())
                    keyAddText?.let { stickerText?.setText(it) }
                    if (keyColorText == 0) {
                        stickerText?.setFontAndColor(this, null, R.color.colorBlack)
                    } else {
                        keyColorText?.let { stickerText?.setFontAndColor(this, null, it) }
                    }
                    stickerText?.post {
                        stickerText?.isRotationHozizontal
                    }
                    if (stickerText?.parent != null) {
                        (stickerText?.parent as ViewGroup).removeView(stickerText)
                    }
                    frameRoot.addView(stickerText)
                }
            }
        }
    }

    override fun onDrawViewTouchUp() {
        //convert draw to bitmap when touch up
        val stickerImageView = StickerImageView(this)
        stickerImageView.isEnabled = false
        stickerImageView.setStickerListener(this)
        stickerImageView.setImageBitmap(drawView.getBitmapNeedToCut())
        frameRoot.addView(
            stickerImageView, drawView.getPairSizeBitmapNeedToCut().first,
            drawView.getPairSizeBitmapNeedToCut().second
        )
        stickerImageView.x = drawView.getPairPositionToPutBitmapInFrame().first.toFloat()
        stickerImageView.y = drawView.getPairPositionToPutBitmapInFrame().second.toFloat()
        listSticker.firstOrNull { it.isChoose }?.isChoose = false
        listSticker.add(StickerModel(stickerImageView, true, false))
        stickerFocus = listSticker.firstOrNull { it.isChoose }?.stickerView
        width = drawView.getPairSizeBitmapNeedToCut().first
        height = drawView.getPairSizeBitmapNeedToCut().second

        Log.e("TAG" , "width height first $width $height")
        if (stickerFake?.parent == frameRoot) {
            frameRoot.removeView(stickerFake)
            frameRoot.addView(
                stickerFake,
                drawView.getPairSizeBitmapNeedToCut().first,
                drawView.getPairSizeBitmapNeedToCut().second
            )
            stickerFake?.x = drawView.getPairPositionToPutBitmapInFrame().first.toFloat()
            stickerFake?.y = drawView.getPairPositionToPutBitmapInFrame().second.toFloat()
            stickerFake?.visibility = View.VISIBLE
        }
        drawView.isEnabled = false
        drawView.visibility = View.GONE

    }
    var stickerFocus: StickerView?= null
    override fun onStickerChoose(sticker: StickerView) {
        //when choose sticker
//        stickerFocus = listSticker.firstOrNull { it.isChoose }?.stickerView
        stickerFocus?.x = sticker.x
        stickerFocus?.y = sticker.y
        stickerFocus?.rotation = sticker.rotation
        stickerFocus?.layoutParams = sticker.layoutParams
        stickerFocus?.requestLayout()

    }

    override fun onScaleSticker(sticker: StickerView) {
        Log.e("TAG", "width height ${sticker.width} + ${sticker.height}")
//        stickerFocus?.layoutParams?.width = sticker.width
//        stickerFocus?.layoutParams?.height = sticker.height
//        stickerFocus?.requestLayout()
    }

    override fun onStickerFlipClicked() {
        val oa1 = ObjectAnimator.ofFloat(stickerFocus, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(stickerFocus, "scaleX", 0f, 1f)
        oa1.duration = 200
        oa2.duration = 200
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                stickerFocus?.rotationY = if (stickerFocus?.rotationY == -180f) 0f else -180f
                stickerFocus?.invalidate()
                oa2.start()
            }
        })
        oa1.start()
        oa2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
            }
        })
        stickerFocus?.requestLayout()
    }

    override fun onStickerActionUp() {
        stickerFocus?.width?.let { width = it }
        stickerFocus?.height?.let { height = it }
    }

    override fun onButtonDrawClicked(button: String) {
        when(button){
            "Draw1" -> {

            }
            "Draw2" -> {

            }
            "Draw3" -> {

            }
            "Draw4" -> {

            }
            "Draw5" -> {

            }
        }
    }
}