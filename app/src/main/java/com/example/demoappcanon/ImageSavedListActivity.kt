package com.example.demoappcanon

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_image_saved_list.*

class ImageSavedListActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val KEY_RESTORE = "KEY"
        const val VALUE_RESTORE = "VALUE"

        const val REQUEST_GALLERY = 301
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_saved_list)
        sharedPreferences = getSharedPreferences(MainActivity.NAME, Context.MODE_PRIVATE)
        var string = sharedPreferences.getString(MainActivity.IMAGE_DEMO, "")

//        if (string!!.isNotEmpty()){
//            imgSave.setImageBitmap(MainActivity.stringToBitmap(string))
//        }else{
//            imgSave.setImageResource(R.drawable.ic_scale)
//        }

        imgSave.setTextColor(Color.BLUE)
        imgSave.setStroke(5F, Color.WHITE)

        requestPermission(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY)
        imgSave.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(KEY_RESTORE, VALUE_RESTORE)
            startActivity(intent)
            finish()
        }

        goToEdit.setOnClickListener {
            clearSharePreferent()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun requestPermission(permission: Array<String>, requestCode: Int) {
        when (requestCode) {
            REQUEST_GALLERY -> {
                if ((ActivityCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(this, permission[1]) == PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(this, "let go", Toast.LENGTH_SHORT).show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(permission[0], permission[1]),
                        requestCode
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GALLERY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Let go", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Not let go", Toast.LENGTH_SHORT).duration
                }
            }
        }
    }

    fun clearSharePreferent(){
        sharedPreferences.edit().clear().apply()
    }
}