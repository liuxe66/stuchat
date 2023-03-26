package com.redcat.stuchat.ui

import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.leading.LeadingLoadStateAdapter
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.liuxe.lib_common.utils.LogUtils
import com.opensource.svgaplayer.SVGAImageView
import com.redcat.stuchat.R
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.base.BaseDataBindingActivity
import com.redcat.stuchat.databinding.ActivityMainBinding
import com.redcat.stuchat.ext.*
import kotlinx.coroutines.delay


class MainActivity : BaseDataBindingActivity() {

    private val mBinding by binding<ActivityMainBinding>(R.layout.activity_main)

    private val mainVM by createViewModel<MainVM>()

    private lateinit var mRecordAdapter: RecordAdapter

    override fun init(savedInstanceState: Bundle?) {

        mBinding.apply {
            svgView.visible()
            svgView.clearsAfterDetached = true
            svgView.loops = 1
            svgView.fillMode = SVGAImageView.FillMode.Clear

            mainVM.initData()
            mainVM.initFinish.observe(this@MainActivity, Observer {
                mainVM.sayHello()
            })
            mainVM.recordData.observe(this@MainActivity, Observer {
                mRecordAdapter.submitList(it)
                mRecordAdapter.notifyDataSetChanged()
            })
            mainVM.scrollToBottom.observe(this@MainActivity, Observer {
                mBinding.rvMsg.scrollToPosition(mainVM.recordList.size - 1)
            })
            mainVM.playSvga.observe(this@MainActivity, Observer {
                mBinding.svgView.loadAssetsSVGA(fileName = getSvga(it), func = {
                    mainVM.insertRecord(AppConfig.type_sys_text, text = "牛，牛哇，啾咪！")
                })
            })

            mainVM.intoRoom.observe(this@MainActivity, Observer {
                LogUtils.e("=========intoRoom========")
                svgView.showAssetsRide(
                    "veh_falali", 1, AppConfig.getPhoto(it.avatar), it.nickName, 10
                )
            })

            var tfRegular = Typeface.createFromAsset(assets, "font/karla_bold.ttf");
            tvTitle.typeface = tfRegular
            tvTitle.text = "Dear·Yhaha"

            llBottom.throttleClick {
                mainVM.randomEvent()
            }
            mRecordAdapter = RecordAdapter()
            mRecordAdapter.mListener = object : RecordAdapter.RecordAdapterListener {
                override fun onLoadFinish(position: Int) {
                    mainVM.updateRecord(position)
                }
            }

            val layoutManager = LinearLayoutManager(this@MainActivity)
            rvMsg.layoutManager = layoutManager

            val helper = QuickAdapterHelper.Builder(mRecordAdapter)
                // 使用默认样式的首部"加载更多"
                .setLeadingLoadStateAdapter(object : LeadingLoadStateAdapter.OnLeadingListener {
                    override fun onLoad() {
                        LogUtils.e("=====setTrailingLoadStateAdapter=====")
                        // 执行加载更多的操作，通常都是网络请求
                        mainVM.getRecordNext()
                    }

                    override fun isAllowLoading(): Boolean {
                        // 是否允许触发“加载更多”
                        return true
                    }
                }).build()
            helper.trailingLoadStateAdapter?.preloadSize = 2

            rvMsg.adapter = helper.adapter
            rvMsg.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                    if (childAdapterPosition == 0) {
                        outRect.set(0, 40, 0, 40);
                    } else {
                        outRect.set(0, 0, 0, 40);
                    }
                }
            })


        }
    }

    private fun getSvga(image: Int?) = when (image) {
        1 -> "gift_rose"
        2 -> "gift_bear"
        3 -> "gift_boom"
        4 -> "gift_birth"
        5 -> "gift_car"
        6 -> "gift_firework"
        7 -> "gift_rock"
        8 -> "gift_room"
        else -> "gift_rose"
    }

    private fun loadRecord() {
        mainVM.getRecords()

    }

    private val TIME_EXIT = 2000
    private var mBackPressed: Long = 0
    override fun onBackPressed() {
        if (mBinding.llBox.isVisible()) {
            mBinding.llBox.gone()
        } else {
            if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();
            }
        }
    }

}