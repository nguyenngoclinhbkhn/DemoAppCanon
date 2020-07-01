package com.example.demoappcanon

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoappcanon.adapter.AdapterButtonDraw
import com.example.demoappcanon.adapter.AdapterStickerOnImage
import com.example.demoappcanon.custom.DrawView
import com.example.demoappcanon.custom.StickerImageView
import com.example.demoappcanon.custom.StickerTextView
import com.example.demoappcanon.custom.StickerView
import com.example.demoappcanon.model.StickerModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


class MainActivity : AppCompatActivity(), View.OnClickListener, DrawView.OnDrawViewListener,
    StickerView.OnStickerListener, AdapterButtonDraw.OnButtonDrawListener,
    AdapterStickerOnImage.OnStickerImageListListener {
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

    private lateinit var sharePreferences: SharedPreferences

    companion object {
        const val NAME = "NAME"
        const val IMAGE_DEMO = "DEMO"

        const val POSITIONX = "PositionX"
        const val POSITIONY = "PositionY"
        const val WIDTH = "Width"
        const val HEIGHT = "Height"
        const val ROTATION = "Rotation"
        const val BITMAP = "Bitmap"
        const val IS_STICKER_IMAGE = "IS_"
        const val SIZE = "SIZE"
        const val KEY_PATH = "Path"

        fun bitmapToString(bitmap: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun stringToBitmap(encodedString: String?): Bitmap? {
            return try {
                val encodeByte =
                    Base64.decode(encodedString, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: Exception) {
                e.message
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharePreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE)



        listSticker = arrayListOf()
        drawView = findViewById(R.id.drawView)
        frameRoot = findViewById(R.id.frameRoot)
        imgPreview = findViewById(R.id.imgPreView)
        drawView.setOnClickListener(this)
        drawView.setOnDrawViewListener(this)
        stickerFake = StickerImageView(this)
        stickerFake?.setStickerListener(this)
        frameRoot.addView(stickerFake)
        stickerFake?.visibility = View.GONE

        disableDrawView()


        val options = BitmapFactory.Options()
//        options.inScaled = false
//        bitmapOrigin = BitmapFactory.decodeResource(resources, R.drawable.test, options).copy(Bitmap.Config.ARGB_8888, true)

        bitmapOrigin = BitmapFactory.decodeResource(resources, R.drawable.test).copy(Bitmap.Config.ARGB_8888, true)

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

            val valueRestore = intent.getStringExtra(ImageSavedListActivity.KEY_RESTORE)
            valueRestore?.let {
                if (it.isNotEmpty()) {
                    restore()
                    Log.e("TAG", "Restore ne")
                }
            }

//            frameRoot.addView(imgPreview, layoutParams)
//            frameRoot.addView(drawView, FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT)
        }

        val btnAddText = findViewById<Button>(R.id.btnAddText).setOnClickListener {
            startActivityForResult(Intent(this, AddTextActivity::class.java), REQUEST_CODE)
        }

        stickerText = StickerTextView(this)
        stickerText?.setStickerListener(this)


//        if (drawView.parent != null) {
//            (drawView.parent as ViewGroup).removeView(drawView)
//        }
//        frameRoot.addView(drawView, FrameLayout.LayoutParams.MATCH_PARENT,
//        FrameLayout.LayoutParams.MATCH_PARENT)


        adapterButton = AdapterButtonDraw(this)
        recyclerDraw.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerDraw.adapter = adapterButton

        adapterStickerOnImage = AdapterStickerOnImage(this)
        recyclerSticker.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerSticker.adapter = adapterStickerOnImage
        adapterStickerOnImage.setList(listSticker)


        sticker.setOnClickListener(this)
        save.setOnClickListener(this)
        saveBitmap.setOnClickListener(this)


    }

    private fun restore() {
        if (sharePreferences == null) {
            sharePreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE)
        }
        val size = sharePreferences.getInt(SIZE, 0)
        if (size > 0) {
            for (i in 0 until size) {
                val positionX = sharePreferences.getFloat("$POSITIONX$i", 0F)
                val positionY = sharePreferences.getFloat("$POSITIONY$i", 0F)
                val width = sharePreferences.getInt("$WIDTH$i", 0)
                val height = sharePreferences.getInt("$HEIGHT$i", 0)
                val rotation = sharePreferences.getFloat("$ROTATION$i", 0F)
                val bitmap = stringToBitmap(sharePreferences.getString("$BITMAP$i", ""))

                val stickerImageView = StickerImageView(this)
                stickerImageView.setStickerListener(this)
                frameRoot.addView(stickerImageView)
                stickerImageView.layoutParams.width = width
                stickerImageView.layoutParams.height = height
                stickerImageView.requestLayout()

                stickerImageView.post {
                    stickerImageView.x = positionX
                    stickerImageView.y = positionY
                }

                Log.e("TAG", "position x share $positionX ${stickerImageView.x}")
                Log.e("TAG", "position y share $positionY ${stickerImageView.y}")

                stickerImageView.rotation = rotation
                stickerImageView.setImageBitmap(bitmap)
                listSticker.add(StickerModel(stickerImageView, isChoose = false, isFake = false))
            }
        }
        adapterStickerOnImage.setList(listSticker)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sticker -> {
                listSticker.forEach {
                    Log.e("TAG", "position x old ${it.stickerView.x}")
                    Log.e("TAG", "position y old ${it.stickerView.y}")
                }
//                val stickerImageView = StickerImageView(this)
//                stickerImageView.setStickerListener(this)
//                stickerImageView.setImageResource(R.drawable.husky)
//                frameRoot.addView(stickerImageView)
//                listSticker.add(
//                    StickerModel(
//                        stickerImageView,
//                        true,
//                        false
//                    )
//                )
//                frameRoot.addView(stickerText)
//                stickerFocus = stickerImageView
//                makeStickerFake(stickerImageView.width,
//                    stickerImageView.height,
//                    stickerImageView.x,
//                    stickerImageView.y)


            }

            R.id.save -> {
                showDialog()
                Single.create<String> {
                    saveElementToSharePreferent()
                    it.onSuccess("OK")
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableSingleObserver<String>() {
                        override fun onSuccess(t: String) {
                            closeDialog()
                            finish()
                        }

                        override fun onError(e: Throwable) {
                        }

                    })
            }
            R.id.saveBitmap -> {
                showDialog()
                Single.create<String> {
                    drawStickerOnBitmapVersion2(bitmapOrigin)
                    saveBitmap(bitmapOrigin)
                    it.onSuccess(fileBitmap.absolutePath)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object: DisposableSingleObserver<String>() {
                        override fun onSuccess(t: String) {
                            closeDialog()
                            val intent = Intent(this@MainActivity, BitmapSavedActivity::class.java)
                            intent.putExtra(KEY_PATH, t)
                            startActivity(intent)
//                            finish()
                        }

                        override fun onError(e: Throwable) {
                            Log.e("TAG", "Error save ${e.toString()}" )
                        }

                    })
            }
        }
    }

    private fun saveElementToSharePreferent() {
        sharePreferences.edit().putString(IMAGE_DEMO, bitmapToString(bitmapResize)).commit()
        sharePreferences.edit().putInt(SIZE, listSticker.size).commit()
        for (i in 0 until listSticker.size) {
            if (listSticker[i].stickerView is StickerImageView) {
                sharePreferences.edit().putFloat("$POSITIONX$i", listSticker[i].stickerView.x)
                    .commit()
                sharePreferences.edit().putFloat("$POSITIONY$i", listSticker[i].stickerView.y)
                    .commit()
                sharePreferences.edit().putInt("$WIDTH$i", listSticker[i].stickerView.width)
                    .commit()
                sharePreferences.edit().putInt("$HEIGHT$i", listSticker[i].stickerView.height)
                    .commit()
                sharePreferences.edit().putFloat("$ROTATION$i", listSticker[i].stickerView.rotation)
                    .commit()
                sharePreferences.edit().putString(
                    "$BITMAP$i",
                    bitmapToString(listSticker[i].stickerView.getBitmapFromView()!!)
                ).commit()

            } else {

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
                    val keyRotation = data?.getBooleanExtra("KEY_ROTATION_TEXT", false)
                    Log.e("KEY_ADD_TEXT", keyAddText)
                    Log.e("KEY_COLOR_TEXT", keyColorText.toString())
                    Log.e("KEY_ROTATION_TEXT", keyRotation.toString())
                    keyAddText?.let { stickerText?.setText(it) }
                    if (keyColorText == 0) {
                        stickerText?.setFontAndColor(this, null, R.color.colorBlack)
                    } else {
                        keyColorText?.let { stickerText?.setFontAndColor(this, null, it) }
                    }
//                    stickerText?.post {
//                        stickerText?.isRotationHozizontal
//                    }
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
        stickerImageView.setGoneBorderAndButton()
        stickerImageView.setStickerListener(this)
        stickerImageView.setImageBitmap(drawView.getBitmapNeedToCut())
        frameRoot.addView(
            stickerImageView, drawView.getPairSizeBitmapNeedToCut().first,
            drawView.getPairSizeBitmapNeedToCut().second
        )
        stickerImageView.x = drawView.getPairPositionToPutBitmapInFrame().first.toFloat()
        stickerImageView.y = drawView.getPairPositionToPutBitmapInFrame().second.toFloat()
        listSticker.firstOrNull { it.isChoose }?.isChoose = false
        listSticker.add(
            StickerModel(
                stickerImageView,
                true,
                false
            )
        )
        stickerFocus = listSticker.first { it.isChoose }.stickerView
        width = drawView.getPairSizeBitmapNeedToCut().first
        height = drawView.getPairSizeBitmapNeedToCut().second

        Log.e("TAG", "width height first $width $height")
        makeStickerFake(
            drawView.getPairSizeBitmapNeedToCut().first,
            drawView.getPairSizeBitmapNeedToCut().second,
            drawView.getPairPositionToPutBitmapInFrame().first.toFloat(),
            drawView.getPairPositionToPutBitmapInFrame().second.toFloat(),
            0F
        )
        stickerFake?.setVisiableBorderAndButton()
        disableDrawView()

    }

    private fun makeStickerFake(
        width: Int, height: Int, positionX: Float, positionY: Float,
        rotation: Float
    ) {
        if (stickerFake?.parent == frameRoot) {
            frameRoot.removeView(stickerFake)
            frameRoot.addView(
                stickerFake,
                width,
                height
            )
            stickerFake?.x = positionX
            stickerFake?.y = positionY
            stickerFake?.rotation = rotation
            stickerFake?.visibility = View.VISIBLE
        }
        stickerFake?.setVisiableBorderAndButton()
    }

    lateinit var stickerFocus: StickerView
    override fun onStickerChoose(sticker: StickerView) {
        //when choose sticker
        stickerFocus = listSticker.first { it.isChoose }.stickerView
        stickerFocus.x = sticker.x
        stickerFocus.y = sticker.y
        stickerFocus.rotation = sticker.rotation
        stickerFocus.layoutParams = sticker.layoutParams
        stickerFocus.requestLayout()

    }

    override fun onScaleSticker(sticker: StickerView) {
        stickerFocus.x = sticker.x
        stickerFocus.y = sticker.y
        stickerFocus.layoutParams = sticker.layoutParams
        stickerFocus.requestLayout()
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
                stickerFocus?.mainView?.rotationY =
                    if (stickerFocus?.mainView?.rotationY == -180f) 0f else -180f
                stickerFocus?.mainView?.invalidate()
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


    override fun onButtonDrawClicked(button: String) {
        when (button) {
            "Draw1" -> {
                drawView.setDraw1()
            }
            "Draw2" -> {
                drawView.setDraw2()
            }
            "Draw3" -> {
                drawView.setDraw3()
            }
            "Draw4" -> {
                drawView.setDraw4()
            }
            "Draw5" -> {
                drawView.setDraw5()
            }
        }
        enableDrawView()
    }

    override fun onStickerImageOnListClicked(stickerModel: StickerModel) {
        disableDrawView()
        listSticker.firstOrNull { it.isChoose }?.isChoose = false
        val stickerModel = listSticker.first { it.stickerView == stickerModel.stickerView }
        stickerModel.isChoose = true
        stickerFocus = stickerModel.stickerView
        makeStickerFake(
            stickerFocus.width, stickerFocus.height,
            stickerFocus.x, stickerFocus.y,
            stickerFocus.rotation
        )
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }


    private fun enableDrawView() {
        drawView.isEnabled = true
        drawView.visibility = View.VISIBLE
    }

    private fun disableDrawView() {
        drawView.isEnabled = false
        drawView.visibility = View.GONE
    }

    private fun removeView(view: View) {
        if (view.parent == frameRoot) {
            frameRoot.removeView(view)
        }
    }


    private var progressDialog: ProgressDialog? = null
    private fun showDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
        }
        progressDialog!!.show()
    }

    private fun closeDialog() {
        Log.e("TAG", "vao close ne")
        if (progressDialog != null) {
            progressDialog!!.cancel()
        }
    }


     private fun drawStickerOnBitmapVersion2(bitmap: Bitmap) {
        val widthBitmap = bitmap.width
        val heightBitmap = bitmap.height
        val paint = Paint();
        val width = (frameRoot.width - imgPreview.width) / 2;
        val height = (frameRoot.height - imgPreview.height) / 2;
        val canvas = Canvas(bitmap)
        for (i in 0 until listSticker.size) {
            val matrix = Matrix()
            matrix.setRotate(listSticker[i].stickerView.rotation)
            val bitmap2 = listSticker[i].stickerView.getBitmapFromView()
            val bitmap3 =
                Bitmap.createBitmap(bitmap2!!, 0, 0, bitmap2.width, bitmap2.height, matrix, true)
            val resized = Bitmap.createScaledBitmap(
                bitmap3,
                bitmap3.width * widthBitmap / imgPreview.width,
                bitmap3.height * heightBitmap / imgPreview.height, true
            )
            bitmap3.recycle()
            var degree = listSticker[i].stickerView.rotation * Math.PI / 180
            if (abs(degree) > Math.PI / 2){
                degree = Math.PI - abs(degree)
            }
            val valueY = listSticker[i].stickerView.width * ((sin(degree) * cos(degree)) /
                    (sin(degree) + cos(degree) + 1))
            val valueX = listSticker[i].stickerView.height * ((sin(degree) * cos(degree)) /
                    (sin(degree) + cos(degree) + 1))


            Log.e("TAG", "value Y $valueY")
            Log.e("TAG", "valueX $valueX")

//            val valueY = 0
//            val valueX = 0
            val x = listSticker[i].stickerView.x - abs(valueX) - width
            val y = listSticker[i].stickerView.y - abs(valueY) - height
            val coordinateX1 = ((x * widthBitmap)/ imgPreview.width)
            val coordinateY1 = ((y * heightBitmap) / imgPreview.height)
            canvas.drawBitmap(resized, coordinateX1.toFloat(), coordinateY1.toFloat(), paint)

        }


    }


    lateinit var fileBitmap: File
    private fun saveBitmap(bitmap: Bitmap){
        val fileName = File(Environment.getExternalStorageDirectory(), "Canon")
        if (!fileName.exists()){
            fileName.mkdirs()
        }
        fileBitmap = File(fileName, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(fileBitmap)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Log.e("TAG", "Save ok")

    }

}