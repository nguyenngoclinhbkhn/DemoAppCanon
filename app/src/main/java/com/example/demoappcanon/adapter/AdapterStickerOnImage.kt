package com.example.demoappcanon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoappcanon.R
import com.example.demoappcanon.custom.StickerView

class AdapterStickerOnImage(val onStickerImageListListener: OnStickerImageListListener): RecyclerView.Adapter<AdapterStickerOnImage.StickerHolder>() {
    private lateinit var inflater: LayoutInflater
    private var listSticker = arrayListOf<StickerView>()


    fun setList(list: ArrayList<StickerView>){
        this.listSticker = list
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
        holder.img.setOnClickListener{
            onStickerImageListListener.onStickerImageOnListClicked()
            notifyDataSetChanged()
        }
    }

    class StickerHolder(val item: View): RecyclerView.ViewHolder(item){
        val img = item.findViewById<ImageView>(R.id.imgItemSticker)
    }

    interface OnStickerImageListListener{
        fun onStickerImageOnListClicked()
    }
}