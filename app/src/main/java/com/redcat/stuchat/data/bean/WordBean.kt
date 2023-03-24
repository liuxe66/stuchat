package com.redcat.stuchat.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 *  author : liuxe
 *  date : 2023/3/22 16:58
 *  description :
 */

data class WordBean(
    val name: String? = null,
    val trans: List<String?>? = null,
    val ukphone: String? = null,
    val usphone: String? = null
) : Serializable