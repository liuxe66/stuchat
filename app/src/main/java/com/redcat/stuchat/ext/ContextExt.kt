package com.redcat.stuchat.ext

import android.content.Context
import com.redcat.stuchat.R


/**
 * context是否销毁
 */
fun Context.checkDestroy() = try {
    getString(R.string.app_name)
    true
} catch (e: Exception) {
    false
}