package com.example.demoappcanon.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demoappcanon.R
import com.example.demoappcanon.custom.SquareTextView

class AdapterColorAddText(val context: Context, val onClickColor: OnClickColorListener) : RecyclerView.Adapter<AdapterColorAddText.ColorHolder>(){
    private val listColors = arrayListOf<Int>(R.color.colorBlue, R.color.colorGray, R.color.colorGreen, R.color.colorRed, R.color.colorYellow, R.color.colorPrimary, R.color.colorSnow)
    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_color, parent, false)

        view.post {
            var height = view.height
            view.layoutParams.width = height
            view.requestLayout()
            Log.e("TAG", "height view $height")
        }
        return ColorHolder(view)
    }

    override fun getItemCount(): Int {
        return listColors.size
    }

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.colorItem.setBackgroundColor(Color.parseColor(context.resources.getString(listColors[position])))
        holder.colorItem.setCornerRadius(context.resources.getDimensionPixelOffset(R.dimen.radius_color))
        holder.colorItem.setOnClickListener{
            onClickColor.onItemColorClicked(listColors[position])
        }
    }

    class ColorHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val colorItem = itemView.findViewById<SquareTextView>(R.id.ivColor)
    }

    interface OnClickColorListener{
        fun onItemColorClicked(color: Int)
    }
}