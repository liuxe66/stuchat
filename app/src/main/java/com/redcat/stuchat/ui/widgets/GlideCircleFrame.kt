package com.redcat.stuchat.ui.widgets


import android.content.res.Resources
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * glide加载图片增加圆形边框
 */
class GlideCircleFrame(
        private val borderWidth: Int = 2,
        private val borderColor: Int = Color.WHITE
) : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(getId().toByteArray())
    }

    private val mBorderPaint: Paint by lazy {
        Paint().apply {
            isDither = true
            isAntiAlias = true
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = mBorderWidth
        }
    }
    private val mBorderWidth by lazy { Resources.getSystem().displayMetrics.density * borderWidth }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return circleFrameCrop(pool, toTransform)
    }

    private fun circleFrameCrop(pool: BitmapPool, source: Bitmap): Bitmap {

        val size = (source.width.coerceAtMost(source.height) - (mBorderWidth / 2)).toInt()
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squared = Bitmap.createBitmap(source, x, y, size, size)

        val result: Bitmap = pool[size, size, Bitmap.Config.ARGB_8888]
                ?: Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)

        canvas.drawCircle(r, r, r - mBorderWidth / 2, mBorderPaint)
        return result
    }

    private fun getId() = this::class.java.name
}