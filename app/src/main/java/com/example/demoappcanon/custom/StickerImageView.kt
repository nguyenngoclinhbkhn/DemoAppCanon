package com.example.demoappcanon.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView


class StickerImageView : StickerView {
    var ownerId: String? = null
    private var iv_main: ImageView ?= null

    constructor(context: Context) : super(context) {

    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override val mainView: View?
        get() {
            if (iv_main == null) {
                iv_main = ImageView(context)
            }
            return iv_main
        }

    fun setImageResource(res_id: Int) {
        iv_main!!.setImageResource(res_id)
    }

    fun setImageDrawable(drawable: Drawable?) {
        iv_main!!.setImageDrawable(drawable)
    }

    fun setImageBitmap(bmp: Bitmap?) {
//        iv_main!!.setBackgroundColor(Color.BLUE)
        Log.e("TAG" ," ivmain $iv_main")
        iv_main!!.setImageBitmap(bmp)
    }

    fun getBitmap(): Bitmap{
        return (iv_main?.drawable as BitmapDrawable).bitmap
    }

}