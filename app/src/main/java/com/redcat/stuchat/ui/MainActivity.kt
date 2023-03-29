package com.redcat.stuchat.ui

import android.graphics.Rect
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaSync
import android.media.MediaSync.OnErrorListener
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.leading.LeadingLoadStateAdapter
import com.liuxe.lib_common.utils.LogUtils
import com.opensource.svgaplayer.SVGAImageView
import com.redcat.stuchat.R
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.base.BaseDataBindingActivity
import com.redcat.stuchat.databinding.ActivityMainBinding
import com.redcat.stuchat.ext.*
import java.io.IOException


class MainActivity : BaseDataBindingActivity() {

    private val mBinding by binding<ActivityMainBinding>(R.layout.activity_main)

    private val mainVM by createViewModel<MainVM>()

    private lateinit var mRecordAdapter: RecordAdapter

    var isWordPlaying = false

    override fun init(savedInstanceState: Bundle?) {

        mBinding.apply {
            svgView.visible()
            svgView.clearsAfterDetached = true
            svgView.loops = 1
            svgView.fillMode = SVGAImageView.FillMode.Clear

            mainVM.apply {
                initData()
                initFinish.observe(this@MainActivity, Observer {
                    //数据初始化结束，欢迎语
                    mainVM.sayHello()
                })
                recordData.observe(this@MainActivity, Observer {
                    mRecordAdapter.submitList(it)
                    mRecordAdapter.notifyDataSetChanged()
                    mRecordAdapter.setOnItemClickListener { adapter, view, position ->
                        when (it[position].type) {
                            AppConfig.type_sys_word -> {
                                playOnlineSound("https://dict.youdao.com/dictvoice?audio=${it[position].wordName}&type=2")
                            }
                        }
                    }
                })
                scrollToBottom.observe(this@MainActivity, Observer {
                    rvMsg.scrollToPosition(mainVM.recordList.size - 1)
                })
                playSvga.observe(this@MainActivity, Observer {
                    if (!mainVM.isLearnWord) {
//                        svgView.loadAssetsSVGA(fileName = AppConfig.getSvga(it), func = {
//                            mainVM.insertRecord(AppConfig.type_sys_text, text = "牛，牛哇，啾咪！")
//                        })
                    }

                })
                intoRoom.observe(this@MainActivity, Observer {
                    LogUtils.e("=========intoRoom========")
                    if (!mainVM.isLearnWord) {
                        if (AppConfig.getVehSvga(it.veh) != "") {
                            svgView.showAssetsRide(
                                AppConfig.getVehSvga(it.veh),
                                1,
                                AppConfig.getPhoto(it.avatar),
                                it.nickName,
                                10
                            )
                        }
                    }


                })
                isLearnWordData.observe(this@MainActivity, Observer {
                    if (it) {
                        mainVM.creatWord()
                        tvSwitch.text = "放松一下"
                        tvNext.visible()
                    } else {
                        tvSwitch.text = "开始学习"
                        tvNext.gone()
                    }
                })
            }


            var tfRegular = Typeface.createFromAsset(assets, "font/karla_bold.ttf");
            tvTitle.typeface = tfRegular
            tvTitle.text = "呀哈哈·自习室"

            //切换模式
            tvSwitch.throttleClick {
                mainVM.changeLearnModel()
            }
            //下一个单词
            tvNext.throttleClick {
                mainVM.creatWord()
            }

            //页面适配器
            mRecordAdapter = RecordAdapter()



            //聊天列表
            val layoutManager = LinearLayoutManager(this@MainActivity)
            rvMsg.layoutManager = layoutManager

            val helper = QuickAdapterHelper.Builder(mRecordAdapter)
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
                    val childCount = parent.adapter!!.itemCount
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                    if (childAdapterPosition == childCount - 1) {
                        outRect.set(0, 0, 0, 10.dp().toInt());
                    } else {
                        outRect.set(0, 0, 0, 0);
                    }
                }
            })

        }
    }

    /**
     * 播放单词发音
     * @param soundUrlDict String
     */
    private fun playOnlineSound(soundUrlDict: String) {
        if (!isWordPlaying) {
            isWordPlaying = true
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(soundUrlDict)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener { mediaPlayer -> mediaPlayer.start() }
                mediaPlayer.setOnCompletionListener { mp ->
                    mp?.release()
                    isWordPlaying = false
                }
                mediaPlayer.setOnErrorListener { p0, p1, p2 ->
                    isWordPlaying = false
                    false
                }
            } catch (e: IOException) {
                isWordPlaying = false
            }
        }

    }


    private val TIME_EXIT = 2000
    private var mBackPressed: Long = 0
    override fun onBackPressed() {
        if (mBinding.llBox.isVisible()) {
            mBinding.llBox.gone()
        } else {
            if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();
            }
        }
    }

}