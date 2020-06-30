package com.example.demoappcanon

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_image_saved_list.*

class ImageSavedListActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    companion object{
        const val KEY_RESTORE = "KEY"
        const val VALUE_RESTORE = "VALUE"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_saved_list)
        sharedPreferences = getSharedPreferences(MainActivity.NAME, Context.MODE_PRIVATE)
        var string = sharedPreferences.getString(MainActivity.IMAGE_DEMO, "")

        if (string!!.isNotEmpty()){
            imgSave.setImageBitmap(MainActivity.stringToBitmap(string))
        }else{
            imgSave.setImageResource(R.drawable.ic_scale)
        }

        imgSave.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(KEY_RESTORE, VALUE_RESTORE)
            startActivity(intent)
            finish()
        }

        goToEdit.setOnClickListener {
            clearSharePreferent()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun clearSharePreferent(){
        sharedPreferences.edit().clear().apply()
    }
}