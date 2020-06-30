package com.example.demoappcanon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoappcanon.adapter.AdapterColorAddText
import com.example.demoappcanon.custom.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_add_text.*


class AddTextActivity : AppCompatActivity(), View.OnClickListener {

    private var etText: EditText? = null
    private var colorAdapter: AdapterColorAddText? = null
    private var colorDefault: Int = 0
    private var isRotation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_text)

        val tvDone = findViewById<TextView>(R.id.tvDone)
        val tvCancel = findViewById<TextView>(R.id.tvCancel)
        val ivRotation = findViewById<ImageView>(R.id.ivRotation)
        etText = findViewById<EditText>(R.id.etText)
        tvCancel.setOnClickListener(this)
        tvDone.setOnClickListener(this)
        ivRotation.setOnClickListener(this)

        colorAdapter = AdapterColorAddText(this, object : AdapterColorAddText.OnClickColorListener{
            override fun onItemColorClicked(color: Int) {
                colorDefault = color
            }
        })
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics).toInt()
        recyColor.addItemDecoration(SpacesItemDecoration(space))
        recyColor.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyColor.adapter = colorAdapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvDone -> {
                val intent = Intent()
                intent.putExtra("KEY_ADD_TEXT", etText?.text.toString())
                intent.putExtra("KEY_COLOR_TEXT", colorDefault)
                intent.putExtra("KEY_ROTATION_TEXT", isRotation)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.tvCancel -> {
                finish()
            }

            R.id.ivRotation -> {
                isRotation = !isRotation
                if (isRotation) ivRotation.setImageDrawable(this.resources.getDrawable(R.drawable.ic_horizon))
                else ivRotation.setImageDrawable(this.resources.getDrawable(R.drawable.ic_ver))
            }
        }
    }
}