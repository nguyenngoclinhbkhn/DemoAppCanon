package com.example.demoappcanon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.demoappcanon.R

class AdapterButtonDraw(val onButtonDrawListener: OnButtonDrawListener) : RecyclerView.Adapter<AdapterButtonDraw.DrawHolder>(){
    private val listButton = arrayListOf<String>("Draw1", "Draw2", "Draw3", "Draw4","Draw5")
    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        return DrawHolder(layoutInflater.inflate(R.layout.item_draw, parent, false))
    }

    override fun getItemCount(): Int {
        return listButton.size
    }

    override fun onBindViewHolder(holder: DrawHolder, position: Int) {
        holder.button.text = listButton[position]

        holder.button.setOnClickListener{
            onButtonDrawListener.onButtonDrawClicked(listButton[position])
        }
    }

    class DrawHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val button = itemView.findViewById<Button>(R.id.btnDraw)
    }

    interface OnButtonDrawListener{
        fun onButtonDrawClicked(button: String)
    }
}