package com.example.demoappcanon.model

import com.example.demoappcanon.custom.StickerView

data class StickerModel(
    var stickerView: StickerView,
    var isChoose: Boolean,
    var isFake: Boolean
) {
    init {
        if (isFake){
            stickerView.setVisiableBorderAndButton()
        }else{
            stickerView.setGoneBorderAndButton()
        }
        stickerView.isEnabled = false
    }

    fun refresh(){
        if (isFake){
            stickerView.setGoneBorderAndButton()
        }else{
            stickerView.setVisiableBorderAndButton()
        }
    }
}