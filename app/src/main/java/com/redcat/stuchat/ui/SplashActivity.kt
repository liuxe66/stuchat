package com.redcat.stuchat.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import com.redcat.stuchat.base.BaseDataBindingActivity
import com.redcat.stuchat.ext.toActivity
import com.redcat.stuchat.utils.Preference

class SplashActivity : BaseDataBindingActivity() {

    private var isFirst:Boolean by Preference(Preference.isFirstLoad,true)
    private val mainVM by createViewModel<MainVM>()
    override fun init(savedInstanceState: Bundle?) {
        if (isFirst){
            mainVM.loadWord().observe(this, Observer {
                mainVM.initData().observe(this, Observer {
                    toActivity<MainActivity>()
                })
                isFirst = false
            })
        } else {
            toActivity<MainActivity>()
        }
    }
}