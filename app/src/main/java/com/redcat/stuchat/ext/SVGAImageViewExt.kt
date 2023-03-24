package com.redcat.stuchat.ext

import androidx.core.view.isVisible
import com.liuxe.lib_common.utils.LogUtils
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL


fun SVGAImageView.loadAssetsSVGA(fileName: String, times: Int = 1) {
    showSVGAFile(fileName, times)
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