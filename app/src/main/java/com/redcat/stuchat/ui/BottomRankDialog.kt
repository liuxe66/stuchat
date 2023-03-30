package com.redcat.stuchat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.liuxe.lib_common.utils.LogUtils
import com.redcat.stuchat.R
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.base.BaseBottomSheetFragment
import com.redcat.stuchat.data.bean.UserBean
import com.redcat.stuchat.databinding.DialogRankBinding
import com.redcat.stuchat.ext.loadAssetsSVGA
import com.redcat.stuchat.utils.PrefUtils

/**
 *  author : liuxe
 *  date : 2023/3/30 15:12
 *  description :
 */
class BottomRankDialog : BaseBottomSheetFragment() {
    private lateinit var mBinding: DialogRankBinding
    private var userListData: ArrayList<UserBean> by PrefUtils(PrefUtils.prefUserList, ArrayList())
    private var userData: UserBean by PrefUtils(PrefUtils.prefUser,UserBean())
    private val mAdapter by lazy { RankAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mBinding = binding(inflater, R.layout.dialog_rank, container)


        var userList = userListData
        if (userData.nickName != ""){
            userList.add(userData)
        }

        userList.sortBy { userBean ->
            userBean.wordNum
        }
        var data = userList.reversed()
        LogUtils.e("userList:$userList")
        mBinding.apply {
            data.getOrNull(0)?.let { user ->

                goldAvatar.load(AppConfig.getPhoto(user.avatar)){
                    transformations(CircleCropTransformation())
                }
                goldSvgView.loadAssetsSVGA(AppConfig.getFrameSvga(user.frame))
                goldNickname.text = user.nickName
                goldWordNum.text = user.wordNum.toString()

            }

            data.getOrNull(1)?.let { user ->
                layoutFamilyRankSilver.let {
                    silverAvatar.load(AppConfig.getPhoto(user.avatar)){
                        transformations(CircleCropTransformation())
                    }
                    silverSvgView.loadAssetsSVGA(AppConfig.getFrameSvga(user.frame))
                    silverNickname.text = user.nickName
                    silverWordNum.text = user.wordNum.toString()
                }
            }

            data.getOrNull(2)?.let { user ->
                layoutFamilyRankBronze.let {
                    bronzeAvatar.load(AppConfig.getPhoto(user.avatar)){
                        transformations(CircleCropTransformation())
                    }
                    bronzeSvgView.loadAssetsSVGA(AppConfig.getFrameSvga(user.frame))
                    bronzeNickname.text = user.nickName
                    bronzeWordNum.text = user.wordNum.toString()
                }
            }

            rvRank.layoutManager = LinearLayoutManager(requireActivity())
            rvRank.adapter = mAdapter
            mAdapter.addAll(data.subList(3,data.size))

        }
        return mBinding.root
    }
}