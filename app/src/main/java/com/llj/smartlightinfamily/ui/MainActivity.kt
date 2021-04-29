package com.llj.smartlightinfamily.ui

import android.animation.ObjectAnimator
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.llj.smartlightinfamily.utils.ToastUtils
import com.llj.smartlightinfamily.MyApplication
import com.llj.smartlightinfamily.bean.UserConfigBean
import com.llj.smartlightinfamily.other.baseObserver
import com.llj.smartlightinfamily.vm.MainViewModel
import com.llj.smartlightinfamily.R
import com.llj.smartlightinfamily.databinding.SmartligthMainBinding
import com.llj.smartlightinfamily.other.FamilyModeEnum
import com.llj.smartlightinfamily.other.WebSocketType

class MainActivity : BaseActivity<SmartligthMainBinding>() {

    override fun getLayoutId() = R.layout.smartligth_main

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
        vm.someoneIndoorPictureIDLiveData.baseObserver(this) {
            getDataBinding().imagePerson.setImageResource(it)
        }
        vm.CControlLiveData.baseObserver(this) {
            if (!it) vm.turnOnDeviceC()
            else vm.turnOffDeviceC()
        }
        vm.DControlLiveData.baseObserver(this) {
            if (!it) vm.turnOnDeviceD()
            else vm.turnOffDeviceD()
        }
        vm.EControlLiveData.baseObserver(this) {
            if (!it) vm.turnOnDeviceE()
            else vm.turnOffDeviceE()
        }
        vm.allControlLiveData.baseObserver(this) {
            if (!it) vm.turnOnAll()
            else vm.turnOffAll()
        }
        vm.familyModeLiveData.baseObserver(this) {
            when (it) {
                FamilyModeEnum.COMMON -> {
                    startAnimators(getDataBinding().ivZhengchang)
                    vm.setCommonMode()
                }
                FamilyModeEnum.SMART -> {
                    startAnimators(getDataBinding().ivSmart)
                    vm.setSmartMode()
                }
                FamilyModeEnum.LEAVE -> {
                    startAnimators(getDataBinding().ivLijia)
                    vm.setCommonMode()
                }
                FamilyModeEnum.REST -> {
                    startAnimators(getDataBinding().ivXiuxi)
                    vm.setCommonMode()
                }
            }
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
        }
        vm.checkUserAndDeviceStatus()
    }

    private fun startAnimators(bedWetting: View) {
        val ax = ObjectAnimator.ofFloat(bedWetting, "scaleX", 1f, 1.5f, 1.0f).apply {
            duration = 300
        }
        val ay = ObjectAnimator.ofFloat(bedWetting, "scaleY", 1f, 1.5f, 1.0f).apply {
            duration = 300
        }
        ax.start()
        ay.start()
    }
}