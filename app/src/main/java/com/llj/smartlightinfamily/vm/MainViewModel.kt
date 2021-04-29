package com.llj.smartlightinfamily.vm

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.llj.smartlightinfamily.R
import com.llj.smartlightinfamily.bean.MainDataBean
import com.llj.smartlightinfamily.bean.ReceiveDeviceBean
import com.llj.smartlightinfamily.bean.UserConfigBean
import com.llj.smartlightinfamily.other.FamilyModeEnum
import com.llj.smartlightinfamily.other.trySuspendExceptFunction
import kotlinx.coroutines.Dispatchers

class MainViewModel(application: Application, accessConfig: UserConfigBean) :
    WebSocketViewModel(accessConfig, application) {

    val allControlLiveData = MutableLiveData<Boolean>()
    val CControlLiveData = MutableLiveData<Boolean>() //卧室灯开关
    val DControlLiveData = MutableLiveData<Boolean>() //客厅灯开关
    val EControlLiveData = MutableLiveData<Boolean>() //卫生间灯开关

    private val _lightLiveData = MutableLiveData<Int>(0) //光照强度
    val lightLiveData: LiveData<Int> = _lightLiveData

    private val _familyModeLiveData = MutableLiveData<FamilyModeEnum>(FamilyModeEnum.COMMON) //光照强度
    val familyModeLiveData: LiveData<FamilyModeEnum> = _familyModeLiveData

    private val _someoneIndoorLiveData = MutableLiveData<Boolean>(false)
    private val someoneIndoorLiveData: LiveData<Boolean> = _someoneIndoorLiveData

    val someoneIndoorPictureIDLiveData: LiveData<Int> = Transformations.map(someoneIndoorLiveData) {
        if (it) R.drawable.preson_in
        else R.drawable.preson_out
    }

    val strFamilyModeLiveData: LiveData<String> = Transformations.map(familyModeLiveData) {
        when (it) {
            FamilyModeEnum.COMMON -> "情景模式(手动)"
            FamilyModeEnum.LEAVE -> "情景模式(离开)"
            FamilyModeEnum.REST -> "情景模式(休息)"
            FamilyModeEnum.SMART -> "情景模式(智能)"
        }
    }

    fun turnOnAll() {
        sendOrderToDevice("A")
    }

    fun turnOffAll() {
        sendOrderToDevice("B")
    }

    fun turnOnDeviceC() { //卧室
        sendOrderToDevice("C:0")
    }

    fun turnOffDeviceC() { //卧室
        sendOrderToDevice("C:255")
    }

    fun turnOnDeviceD() { //客厅
        sendOrderToDevice("D:0")
    }

    fun turnOffDeviceD() { //客厅
        sendOrderToDevice("D:255")
    }

    fun turnOnDeviceE() { //卫生间
        sendOrderToDevice("E:0")
    }

    fun turnOffDeviceE() { //卫生间
        sendOrderToDevice("E:255")
    }

    fun setSmartMode(){
        sendOrderToDevice("G")
    }

    fun setCommonMode(){
        sendOrderToDevice("H")
    }

    fun setMode(fm: FamilyModeEnum) {
        _familyModeLiveData.postValue(fm)
        when (fm) {
            FamilyModeEnum.LEAVE -> {
                allControlLiveData.postValue(false)
            }
            FamilyModeEnum.REST -> {
                CControlLiveData.postValue(false)
                DControlLiveData.postValue(false)
                EControlLiveData.postValue(false)
            }

        }
    }

    fun notifyAnalysisJson(jsonStr: String) = trySuspendExceptFunction(Dispatchers.IO) {
        val gson = Gson()
        val bean = gson.fromJson(jsonStr, ReceiveDeviceBean::class.java)
        bean?.let {
            val jsonObject = it.V
            val isIndoor = jsonObject[isIndoorInterfaceId].asInt
            _someoneIndoorLiveData.postValue(isIndoor == 1)
            val light = jsonObject[lightInterfaceId].asFloat
            val progressValue = (light*100/200).toInt()
            _lightLiveData.postValue(progressValue)
        }
    }

    companion object {
        private const val isIndoorInterfaceId = "19751"
        private const val lightInterfaceId = "19750"
    }

}