package com.redcat.stuchat.ui.widgets

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class GlideCustomTarget<T>(
        private val ready: (T) -> Unit = {},
        private val cleared: (Drawable) -> Unit = {}
) : CustomTarget<T>() {
    override fun onLoadCleared(placeholder: Drawable?) {
        placeholder?.let(cleared)
    }

    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
        ready(resource)
    }
}