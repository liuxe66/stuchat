package com.redcat.stuchat.ui

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.Toast
import androidx.lifecycle.Observer
import com.liuxe.lib_common.utils.LogUtils
import com.opensource.svgaplayer.*
import com.opensource.svgaplayer.SVGAParser.Companion.shareParser
import com.opensource.svgaplayer.utils.log.SVGALogger.setLogEnabled
import com.redcat.stuchat.R
import com.redcat.stuchat.base.BaseDataBindingActivity
import com.redcat.stuchat.databinding.ActivityMainBinding
import com.redcat.stuchat.ext.*
import java.net.MalformedURLException
import java.net.URL
import kotlin.text.Typography.times


class MainActivity : BaseDataBindingActivity() {

    private val mBinding by binding<ActivityMainBinding>(R.layout.activity_main)

    private val mainVM by createViewModel<MainVM>()

    override fun init(savedInstanceState: Bundle?) {

        mBinding.apply {

            initMsgData()
            var tfRegular = Typeface.createFromAsset(assets, "font/karla_bold.ttf");
            tvTitle.typeface = tfRegular
            tvTitle.text = "Dear·Yhaha"

            mainVM.sayHello()
            llBottom.throttleClick {
                loadSvga()
            }

            flMsg.throttleClick {
                llBox.gone()
            }

            svgView.visible()
            svgView.clearsAfterDetached = true
            svgView.loops = 1
            svgView.fillMode = SVGAImageView.FillMode.Clear
        }
    }

    fun loadSvga() {
        mBinding.svgView.loadAssetsSVGA(
            fileName = "rock"
        )
    }

    private fun initMsgData() {
        mainVM.getRecords()
        mainVM.recordData.observe(this, Observer {
            LogUtils.e("list:" + it)
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