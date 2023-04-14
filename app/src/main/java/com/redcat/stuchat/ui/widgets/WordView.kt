package com.redcat.stuchat.ui.widgets

import android.content.Context
import android.graphics.Canvas
import androidx.appcompat.widget.AppCompatTextView

/**
 *  author : liuxe
 *  date : 2023/4/11 13:15Ø
 *  description :
 */
class WordView(content:Context):AppCompatTextView(content) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}