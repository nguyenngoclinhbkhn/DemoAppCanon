package com.example.demoappcanon.custom

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
    }

    fun refresh(){
        if (isFake){
            stickerView.setGoneBorderAndButton()
        }else{
            stickerView.setVisiableBorderAndButton()
        }
    }
}