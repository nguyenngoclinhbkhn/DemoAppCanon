package com.example.demoappcanon.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SpacesItemDecoration(private val space: Int, private val space2: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val position: Int = parent.getChildAdapterPosition(view)
        val isLast = position == state.itemCount - 1
        if (isLast) {
            outRect.right = space2
        }
        if (position == 0) {
            outRect.left = space2
        }
        outRect.bottom = space
        outRect.top = space
        outRect.left = space
        outRect.right = space
    }

}