package com.redcat.stuchat.ui.widgets

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


/**
 *  author : liuxe
 *  date : 4/30/21 4:56 PM
 *  description : recyclerview grid 分割线
 */
class RecycleLinearDivider @JvmOverloads constructor(
    space: Int = 1,
    color: Int = Color.TRANSPARENT
) :
    ItemDecoration() {
    private val space: Int
    private val color: Int
    private var mPaint: Paint? = null
    private fun initPaint() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = color
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.strokeWidth = space.toFloat()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childAdapterPosition = parent.getChildAdapterPosition(view);
        if (childAdapterPosition == 0) {
            outRect.set(0, space, 0, space);
        }  else {
            outRect.set(0, 0, 0, space);
        }
    }


    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDraw(c, parent, state)

    }
    /**
     * 自定义宽度，并指定颜色的分割线
     *
     * @param space 指定宽度
     * @param color 指定颜色
     */
    init {
        this.space = space
        this.color = color
        initPaint()
    }
}
