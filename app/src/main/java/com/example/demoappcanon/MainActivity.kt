package com.example.demoappcanon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoappcanon.adapter.AdapterButtonDraw
import com.example.demoappcanon.adapter.AdapterStickerOnImage
import com.example.demoappcanon.custom.DrawView
import com.example.demoappcanon.custom.StickerImageView
import com.example.demoappcanon.custom.StickerTextView
import com.example.demoappcanon.custom.StickerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, DrawView.OnDrawViewListener,
    StickerView.OnStickerListener, AdapterButtonDraw.OnButtonDrawListener {
    private lateinit var imgPreview : ImageView
    private lateinit var frameRoot: FrameLayout
    private lateinit var stickerRoot: StickerImageView
    private lateinit var drawView: DrawView
    private val widthBitmap = 1600
    private val heightBitmap = 900
    private lateinit var bitmapResize: Bitmap
    private lateinit var bitmapOrigin: Bitmap
    private lateinit var adapterButton: AdapterButtonDraw
    private lateinit var adapterStickerOnImage: AdapterStickerOnImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frameRoot = findViewById(R.id.frameRoot)
        drawView = DrawView(this)
        drawView.setOnClickListener(this)
        imgPreview = ImageView(this)
        drawView.setOnDrawViewListener(this)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x


        bitmapOrigin = BitmapFactory.decodeResource(resources, R.drawable.test)


        frameRoot.post {
            val widthFrame = frameRoot.width
            val heightFrame = frameRoot.height
            bitmapResize = Bitmap.createScaledBitmap(bitmapOrigin,
                widthFrame,
                heightBitmap * widthFrame / widthBitmap,
                true
            )
            imgPreview.setImageBitmap(bitmapResize)

            val layoutParams = FrameLayout.LayoutParams(bitmapResize.width, bitmapResize.height,
            Gravity.CENTER)

            frameRoot.addView(imgPreview, layoutParams)
        }

        frameRoot.addView(drawView, FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)



        adapterButton = AdapterButtonDraw(this)
        recyclerDraw.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerDraw.adapter = adapterButton
        sticker.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
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

            }
        }
    }

    override fun onDrawViewTouchUp() {
        //convert draw to bitmap when touch up

    }

    override fun onStickerChoose(sticker: StickerView) {
        //when choose sticker
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