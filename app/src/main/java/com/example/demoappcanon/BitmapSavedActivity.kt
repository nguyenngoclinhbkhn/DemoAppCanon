package com.example.demoappcanon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_bitmap_saved.*
import kotlinx.android.synthetic.main.activity_main.*

class BitmapSavedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_saved)

        Glide.with(this)
            .load(intent.getStringExtra(MainActivity.KEY_PATH))
            .into(imgBitmapSaved)

    }
}