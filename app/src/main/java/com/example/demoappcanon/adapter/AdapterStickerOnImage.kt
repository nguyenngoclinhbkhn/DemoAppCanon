package com.example.demoappcanon.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoappcanon.R
import com.example.demoappcanon.custom.StickerView
import com.example.demoappcanon.model.StickerModel

class AdapterStickerOnImage(val onStickerImageListListener: OnStickerImageListListener): RecyclerView.Adapter<AdapterStickerOnImage.StickerHolder>() {
    private lateinit var inflater: LayoutInflater
    private var listSticker = arrayListOf<StickerModel>()
    private var index = -1

    fun setList(list: ArrayList<StickerModel>){
        this.listSticker = list
        Log.e("TAG", "liststicker ${list.size}")
        notifyDataSetChanged()
    }

    fun setIndex(indexNew: Int){
        this.index = indexNew
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerHolder {
        inflater = LayoutInflater.from(parent.context)
        return StickerHolder(inflater.inflate(R.layout.item_sticker, parent, false))
    }

    override fun getItemCount(): Int {
        return listSticker.size
    }



    override fun onBindViewHolder(holder: StickerHolder, position: Int) {
        holder.img.setImageBitmap(listSticker[position].stickerView.getBitmapFromView())
        holder.img.setOnClickListener{
            onStickerImageListListener.onStickerImageOnListClicked(listSticker[position])
            index = position
            notifyDataSetChanged()
        }
        if (index == position){
            holder.img.setBackgroundColor(Color.WHITE)
        }else{
            holder.img.setBackgroundColor(Color.GRAY)
        }
    }

    class StickerHolder(val item: View): RecyclerView.ViewHolder(item){
        val img = item.findViewById<ImageView>(R.id.imgItemSticker)
    }

    interface OnStickerImageListListener{
        fun onStickerImageOnListClicked(stickerModel: StickerModel)
    }
}