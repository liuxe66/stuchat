package com.redcat.stuchat.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.redcat.stuchat.R
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.app.RedCatApp
import com.redcat.stuchat.data.bean.UserBean
import com.redcat.stuchat.databinding.ItemRankBinding
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent

/**
 *  author : liuxe
 *  date : 2023/3/30 17:44
 *  description :
 */
class RankAdapter():BaseQuickAdapter<UserBean,RankAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemRankBinding = ItemRankBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int, item: UserBean?) {
       holder.binding?.apply {
           if (item?.userId == 0){

           } else {

           }
           ivPhoto.load(AppConfig.getPhoto(item?.avatar)){
               transformations(CircleCropTransformation())
           }
           tvNickName.text = item?.nickName
           tvWordNum.text = item?.wordNum.toString()
           tvNo.text = (position+4).toString()
       }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
}