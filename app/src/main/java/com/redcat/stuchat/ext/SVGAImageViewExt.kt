package com.redcat.stuchat.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.liuxe.lib_common.utils.LogUtils
import com.opensource.svgaplayer.*
import com.redcat.stuchat.ui.widgets.GlideCircleFrame
import com.redcat.stuchat.ui.widgets.GlideCustomTarget
import java.net.URL


fun SVGAImageView.loadAssetsSVGA(fileName: String, times: Int = 1, func: () -> Unit = {}) {
    showSVGAFile(fileName, times, complete = { func() })
}

inline fun SVGAImageView.showLinkSVGA(
    link: String, times: Int = 0,
    crossinline complete: (videoItem: SVGAVideoEntity) -> Unit = {},
    crossinline error: () -> Unit = {}

) {
    val callback = object : SVGAParser.ParseCompletion {
        override fun onComplete(videoItem: SVGAVideoEntity) {
            if (!context.checkDestroy()) {
                return
            }
            complete(videoItem)
        }

        override fun onError() {
            error()
        }
    }

    loops = times
    isVisible = true

    try {
        SVGAParser(this.context).decodeFromURL(URL(link), callback)
    } catch (t: Throwable) {
        println("TAG_Debug -> showLinkSVGA error $link")
    }
}

inline fun SVGAImageView.showComplete(
    link: String? = null, fileName: String? = null,
    crossinline func: () -> Unit = {}, times: Int = 0
) {

//    clearsAfterStop = true

    fileName?.let { f ->
        showSVGAFile(f, times, complete = { func() })
    }

    link?.let { l ->
        showLinkSVGA(l, times, complete = { videoEntity ->
            setVideoItem(videoEntity)
            startAnimation()
            func()
        })
    }
}

inline fun SVGAImageView.showSVGAFile(
    fileName: String, times: Int = 1,
    crossinline complete: (videoItem: SVGAVideoEntity) -> Unit = {},
    crossinline error: () -> Unit = {}

): SVGAParser.ParseCompletion {
    val callback = object : SVGAParser.ParseCompletion {
        override fun onComplete(videoItem: SVGAVideoEntity) {
            setImageDrawable(SVGADrawable(videoItem))
            startAnimation()
            complete(videoItem)
        }

        override fun onError() {
            LogUtils.e("showSVGAFile  onError ")
            error()
        }
    }
    try {
        loops = times
        SVGAParser(context).decodeFromAssets("$fileName.svga", callback)
    } catch (t: Throwable) {

    }
    return callback
}


fun SVGAImageView.loadLinkSVGA(link: String? = null, times: Int = 1) {

    if (link.isNullOrBlank()) {
        return
    }

    try {
        loops = times
        SVGAParser(context).decodeFromURL(URL(link), object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                setImageDrawable(SVGADrawable(videoItem))
                startAnimation()
            }

            override fun onError() {
                LogUtils.e("loadLinkSVGA  onError ")
            }
        })
    } catch (t: Throwable) {
    }
}


fun SVGAImageView.showLinkRide(
    link: String?,
    times: Int = 1,
    avatar: String? = null,
    userName: String? = null,
    sp: Int = 10
) {

    if (isAnimating) {
        return
    }

    if (link.isNullOrEmpty()) {
        return
    }
    avatar?.let {

        showLinkSVGA(link, times, complete = { videoEntity ->

            if (!context.checkDestroy()) {
                return@showLinkSVGA
            }

            try {


                Glide.with(context)
                    .asBitmap()
                    .centerCrop()
                    .transform(GlideCircleFrame(borderWidth = 4, borderColor = Color.WHITE))
                    .load(avatar)
                    .into(GlideCustomTarget<Bitmap>(ready = { bitmap ->

                        try {
                            val dynamicEntity = SVGADynamicEntity()
                            //设置头像
                            dynamicEntity.setDynamicImage(bitmap, "key_ride_avatar")
//                                dynamicEntity.setDynamicImage(bitmap, "img_joinRoomHasMountsAvatarKey")
                            //设置内容
                            val objPaint = TextPaint()
                            objPaint.color = Color.parseColor("#ffffff")
                            objPaint.textSize = sp.sp()
                            dynamicEntity.setDynamicText(
                                "$userName 进入自习室",
                                objPaint,
                                "key_ride_banner"
                            )
//                                dynamicEntity.setDynamicText("$userName into the room", objPaint, "img_joinRoomTextKey")

                            setImageDrawable(SVGADrawable(videoEntity, dynamicEntity))
                            startAnimation()
                        } catch (e: Throwable) {
                        }

                    }))
            } catch (e: Throwable) {

            }
        })

    } ?: showLinkSVGA(link, times)

}


fun SVGAImageView.showAssetsRide(
    fileName: String,
    times: Int = 1,
    avatar: Int? = null,
    userName: String? = null,
    sp: Int = 10
) {

    if (isAnimating) {
        return
    }

    avatar?.let {

        showSVGAFile(fileName, times, complete = { videoEntity ->
            var bitmap= BitmapFactory.decodeResource(context.resources, avatar)
            val dynamicEntity = SVGADynamicEntity()
            //设置头像
            dynamicEntity.setDynamicImage(bitmap, "key_ride_avatar")
            //设置内容
            val objPaint = TextPaint()
            objPaint.color = Color.parseColor("#ffffff")
            objPaint.textSize = sp.sp()
            dynamicEntity.setDynamicText("$userName 进入自习室", objPaint, "key_ride_banner")

            setImageDrawable(SVGADrawable(videoEntity, dynamicEntity))
            startAnimation()
        })

    } ?: showSVGAFile(fileName, times)


}

fun SVGAImageView.showAssetsAvatar(
    fileName: String,
    times: Int = 0,
    avatar: Int? = null,
) {

    if (isAnimating) {
        return
    }

    avatar?.let {

        LogUtils.e("avatar:"+avatar)
        showSVGAFile(fileName, times, complete = { videoEntity ->
            var bitmap= BitmapFactory.decodeResource(context.resources, avatar)
            val dynamicEntity = SVGADynamicEntity()
            //设置头像
            dynamicEntity.setDynamicImage(bitmap, "key_user_avatar")
            setImageDrawable(SVGADrawable(videoEntity, dynamicEntity))
            startAnimation()
        })

    }
//        ?: showSVGAFile(fileName, times)


}

