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
import com.redcat.stuchat.base.BaseDataBindingActivity
import com.redcat.stuchat.databinding.ActivityMainBinding
import com.redcat.stuchat.ext.*


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
            //先加载聊天记录
            loadRecord()
            //初始化新数据
            initData()

            var tfRegular = Typeface.createFromAsset(assets, "font/karla_bold.ttf");
            tvTitle.typeface = tfRegular
            tvTitle.text = "Dear·Yhaha"

            llBottom.throttleClick {
                loadSvga()
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

    private fun initData() {
        mainVM.sayHello()
    }

    fun loadSvga() {
        mBinding.svgView.loadAssetsSVGA(
            fileName = "rock"
        )
    }

    private fun loadRecord() {
        mainVM.getRecords()
        mainVM.recordData.observe(this, Observer {
            mRecordAdapter.submitList(it)
            mRecordAdapter.notifyDataSetChanged()
        })
        mainVM.scrollToBottom.observe(this, Observer {
            mBinding.rvMsg.scrollToPosition(mainVM.recordList.size - 1)
        })
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