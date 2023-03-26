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
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.liuxe.lib_common.utils.LogUtils
import com.redcat.stuchat.R
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.data.room.entity.Record
import com.redcat.stuchat.databinding.*
import com.redcat.stuchat.utils.DimenUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *Created by Liuxe on 2023/3/25 21:33
 *desc : 记录adapter
 */
class RecordAdapter : BaseMultiItemAdapter<Record>() {

    var mListener: RecordAdapterListener? = null

    class ItemTypeLeftVH(val viewBinding: ItemTypeLeftTextBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
    class ItemTypeLeftImageVH(val viewBinding: ItemTypeLeftImageBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
    class ItemTypeLeftNoticeVH(val viewBinding: ItemTypeLeftNoticeBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
    class ItemTypeRightVH(val viewBinding: ItemTypeRightTextBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
    class ItemTypeRightImageVH(val viewBinding: ItemTypeRightImageBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
    init {
        addItemType(AppConfig.type_sys_text,
            object : OnMultiItemAdapterListener<Record, ItemTypeLeftVH> { // 类型 1
                override fun onCreate(
                    context: Context, parent: ViewGroup, viewType: Int
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

                            tvLeftText.text = item.text

                            if (item.unread == 0) {
                                mListener?.onLoadFinish(item.recordId)
                            }
                        }

                    }
                }
            }).addItemType(AppConfig.type_sys_notice,
            object : OnMultiItemAdapterListener<Record, ItemTypeLeftNoticeVH> { // 类型 1
                override fun onCreate(
                    context: Context, parent: ViewGroup, viewType: Int
                ): ItemTypeLeftNoticeVH {
                    // 创建 viewholder
                    val viewBinding =
                        ItemTypeLeftNoticeBinding.inflate(LayoutInflater.from(context), parent, false)
                    return ItemTypeLeftNoticeVH(viewBinding)
                }

                override fun onBind(holder: ItemTypeLeftNoticeVH, position: Int, item: Record?) {
                    holder.viewBinding.apply {
                        if (item != null) {
                            tvLeftText.text = item.text
                        }

                    }
                }
            }).addItemType(AppConfig.type_sys_pic,
            object : OnMultiItemAdapterListener<Record, ItemTypeLeftImageVH> { // 类型 1
                override fun onCreate(
                    context: Context, parent: ViewGroup, viewType: Int
                ): ItemTypeLeftImageVH {
                    // 创建 viewholder
                    val viewBinding =
                        ItemTypeLeftImageBinding.inflate(LayoutInflater.from(context), parent, false)
                    return ItemTypeLeftImageVH(viewBinding)
                }

                override fun onBind(holder: ItemTypeLeftImageVH, position: Int, item: Record?) {
                    holder.viewBinding.apply {

                        holder.viewBinding.apply {
                            if (item != null) {
                                ivLeftPhoto.load(getPhoto(item?.avatar)) {
                                    transformations(CircleCropTransformation())
                                }
                                tvLeftNick.text = item?.nickName ?: "呀哈哈"

                                ivLeftImage.load(getImage(item?.image)) {
                                    transformations(RoundedCornersTransformation(36f))
                                }

                                if (item.unread == 0) {
                                    mListener?.onLoadFinish(item.recordId)
                                }
                            }

                        }

                    }
                }
            }).addItemType(AppConfig.type_user_text,
            object : OnMultiItemAdapterListener<Record, ItemTypeRightVH> { // 类型 1
                override fun onCreate(
                    context: Context, parent: ViewGroup, viewType: Int
                ): ItemTypeRightVH {
                    // 创建 viewholder
                    val viewBinding =
                        ItemTypeRightTextBinding.inflate(LayoutInflater.from(context), parent, false)
                    return ItemTypeRightVH(viewBinding)
                }

                override fun onBind(holder: ItemTypeRightVH, position: Int, item: Record?) {
//                    LogUtils.e("item:" + item.toString())
                    // 绑定 item 数据
                    var image = getPhoto(item?.avatar)
                    holder.viewBinding.apply {
                        if (item != null) {
                            ivLeftPhoto.load(image) {
                                transformations(CircleCropTransformation())
                            }
                            tvLeftNick.text = item?.nickName ?: "呀哈哈"

                            tvLeftText.text = item.text

                            if (item.unread == 0) {
                                mListener?.onLoadFinish(item.recordId)
                            }
                        }

                    }
                }
            }).addItemType(AppConfig.type_user_pic,
            object : OnMultiItemAdapterListener<Record, ItemTypeRightImageVH> { // 类型 1
                override fun onCreate(
                    context: Context, parent: ViewGroup, viewType: Int
                ): ItemTypeRightImageVH {
                    // 创建 viewholder
                    val viewBinding =
                        ItemTypeRightImageBinding.inflate(LayoutInflater.from(context), parent, false)
                    return ItemTypeRightImageVH(viewBinding)
                }

                override fun onBind(holder: ItemTypeRightImageVH, position: Int, item: Record?) {
                    holder.viewBinding.apply {

                        holder.viewBinding.apply {
                            if (item != null) {
                                ivLeftPhoto.load(getPhoto(item?.avatar)) {
                                    transformations(CircleCropTransformation())
                                }
                                tvLeftNick.text = item?.nickName ?: "呀哈哈"

                                ivLeftImage.load(getImage(item?.image)) {
                                    transformations(RoundedCornersTransformation(36f))
                                }

                                if (item.unread == 0) {
                                    mListener?.onLoadFinish(item.recordId)
                                }
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

    private fun getImage(image: Int?) = when (image) {
        1 -> R.drawable.ic_gift_rose
        2 -> R.drawable.ic_gift_bear
        3 -> R.drawable.ic_gift_boom
        4 -> R.drawable.ic_gift_birth
        5 -> R.drawable.ic_gift_car
        6 -> R.drawable.ic_gift_firework
        7 -> R.drawable.ic_gift_rocket
        8 -> R.drawable.ic_gift_room
        else -> R.drawable.ic_gift_rose
    }

    interface RecordAdapterListener {
        fun onLoadFinish(id: Int)
    }

}