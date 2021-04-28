package com.llj.smartlightinfamily.ui

import android.animation.ObjectAnimator
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.llj.smartlightinfamily.utils.ToastUtils
import com.llj.smartlightinfamily.MyApplication
import com.llj.smartlightinfamily.bean.UserConfigBean
import com.llj.smartlightinfamily.other.WebSocketType
import com.llj.smartlightinfamily.other.baseObserver
import com.llj.smartlightinfamily.vm.MainViewModel
import com.llj.smartlightinfamily.R
import com.llj.smartlightinfamily.databinding.SmartligthLookBinding

class MainActivity : BaseActivity<SmartligthLookBinding>() {

    override fun getLayoutId() = R.layout.smartligth_look

    override fun buildToolbar() = ToolbarConfig("主页", isShowBack = false, isShowMenu = false)

    private val vm by viewModels<MainViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(
                    application,
                    UserConfigBean(
                        MyApplication.userId,
                        MyApplication.appKey,
                        MyApplication.deviceId
                    )
                ) as T
            }
        }
    }

    override fun init() {
        setFullScreen()
        getDataBinding().vm = vm

        vm.toastLiveData.baseObserver(this) {
            ToastUtils.toastShort(it)
        }
        vm.receiveDeviceDataLiveData.baseObserver(this) {
            vm.notifyAnalysisJson(it)
        }

        vm.mainDataBeanLiveData.baseObserver(this) {
            vm.updateUi(it)
        }

        vm.allControlLiveData.baseObserver(this) {
            ToastUtils.toastShort("click:${it.toString()}")
        }
/*
        getDataBinding().switchShake.setOnClickListener {
            val isClick = getDataBinding().switchShake.isChecked
            if (isClick) vm.startShake()
            else vm.stopShake()
        }


        vm.webState.baseObserver(this) { status ->
            when (status) {
                WebSocketType.DEVICE_OFFLINE -> {
                    getDataBinding().tvIsLogined.apply {
                        text = "设备离线"
                        setTextColor(resources.getColor(R.color.red))
                    }
                }
                WebSocketType.DEVICE_ONLINE -> {
                    getDataBinding().tvIsLogined.apply {
                        text = "设备在线"
                        setTextColor(resources.getColor(R.color.green))
                    }
                }
            }
        }*/
//        vm.checkUserAndDeviceStatus()
    }

    private fun startAnimators(bedWetting: View) {
        val ax =ObjectAnimator.ofFloat(bedWetting,"scaleX",1f,1.5f,1.0f).apply {
            duration = 300
        }
        val ay = ObjectAnimator.ofFloat(bedWetting,"scaleY",1f,1.5f,1.0f).apply {
            duration = 300
        }
        ax.start()
        ay.start()
    }
}