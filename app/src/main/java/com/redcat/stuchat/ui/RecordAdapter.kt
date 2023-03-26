package com.redcat.stuchat.ui

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.liuxe.lib_common.utils.LogUtils
import com.redcat.stuchat.R
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.data.room.entity.Record
import com.redcat.stuchat.databinding.ItemTypeLeftImageBinding
import com.redcat.stuchat.databinding.ItemTypeLeftTextBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *Created by Liuxe on 2023/3/25 21:33
 *desc : 记录adapter
 */
class RecordAdapter() : BaseMultiItemAdapter<Record>() {

    var mListener: RecordAdapterListener? = null

    // 类型 1 的 viewholder
    class ItemTypeLeftVH(val viewBinding: ItemTypeLeftTextBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    // 类型 2 的 viewholder
    class ItemTypeLeftImageVH(val viewBinding: ItemTypeLeftImageBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    // 在 init 初始化的时候，添加多类型
    init {
        addItemType(
            AppConfig.type_sys_text,
            object : OnMultiItemAdapterListener<Record, ItemTypeLeftVH> { // 类型 1
                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): ItemTypeLeftVH {
                    // 创建 viewholder
                    val viewBinding =
                        ItemTypeLeftTextBinding.inflate(LayoutInflater.from(context), parent, false)
                    return ItemTypeLeftVH(viewBinding)
                }

                override fun onBind(holder: ItemTypeLeftVH, position: Int, item: Record?) {
//                    LogUtils.e("item:" + item.toString())
                    // 绑定 item 数据
                    var image = getPhoto(item?.avatar)
                    holder.viewBinding.apply {
                        if (item != null) {
                            ivLeftPhoto.load(image) {
                                transformations(CircleCropTransformation())
                            }
                            tvLeftNick.text = item?.nickName ?: "呀哈哈"

                            if (item.unread == 1) {
                                tvLeftText.text = item.text
                            } else {
                                showText(tvLeftText, item?.text)
                            }

                            if (item.unread == 0){
                                mListener?.onLoadFinish(item.recordId)
                            }
                        }

                    }
                }
            }).onItemViewType { position, list -> // 根据数据，返回对应的 ItemViewType
            list[position].type
        }
    }

    private fun showText(tvLeftText: TextView, text: String?) {
        MainScope().launch {
            if (text != null) {
                for (index in text.indices) {
                    tvLeftText.text = text.subSequence(0, index)
                    delay(80)
                }
            }
        }


    }

    private fun getPhoto(image: Int?) = when (image) {
        1 -> R.drawable.ic_avatar1
        2 -> R.drawable.ic_avatar2
        3 -> R.drawable.ic_avatar3
        4 -> R.drawable.ic_avatar4
        5 -> R.drawable.ic_avatar5
        6 -> R.drawable.ic_avatar6
        7 -> R.drawable.ic_avatar_luxun
        else -> R.drawable.ic_logo
    }

    interface RecordAdapterListener {
        fun onLoadFinish(id: Int)
    }

}