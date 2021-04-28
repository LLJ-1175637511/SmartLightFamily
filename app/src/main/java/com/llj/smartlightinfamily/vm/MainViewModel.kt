package com.llj.smartlightinfamily.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.llj.smartlightinfamily.bean.MainDataBean
import com.llj.smartlightinfamily.bean.ReceiveDeviceBean
import com.llj.smartlightinfamily.bean.UserConfigBean
import com.llj.smartlightinfamily.other.trySuspendExceptFunction
import kotlinx.coroutines.Dispatchers

class MainViewModel(application: Application, accessConfig: UserConfigBean) :
    WebSocketViewModel(accessConfig, application) {

    private val _tiltLiveData = MutableLiveData<Boolean>()
    val tiltLiveData: LiveData<Boolean> = _tiltLiveData

    val allControlLiveData = MutableLiveData<Boolean>(false)

    private val _mainDataBeanLiveData = MutableLiveData<MainDataBean>()
    val mainDataBeanLiveData: LiveData<MainDataBean> = _mainDataBeanLiveData

    fun startShake() {
        sendOrderToDevice("A")
    }

    fun stopShake() {
        sendOrderToDevice("B")
    }

    fun notifyAnalysisJson(jsonStr: String) = trySuspendExceptFunction(Dispatchers.IO) {
        val gson = Gson()
        val bean = gson.fromJson(jsonStr, ReceiveDeviceBean::class.java)
        bean?.let {
            val jsonObject = it.V
            val temp = jsonObject[tempInterfaceId].asFloat
            val humi = jsonObject[humiInterfaceId].asFloat
            val isWetting = humi > 30f
            val tilt = jsonObject[tiltInterfaceId].asInt == 1
            val bedHeader = jsonObject[bedHeaderInterfaceId].asInt == 1
            val bedEnd = jsonObject[bedEndInterfaceId].asInt == 1
            _mainDataBeanLiveData.postValue(
                MainDataBean(
                    temp,
                    humi,
                    isWetting,
                    bedHeader,
                    bedEnd,
                    tilt
                )
            )
        }
    }

    fun updateUi(it: MainDataBean) {

        _tiltLiveData.postValue(it.tilt)
    }

    companion object {
        private const val tempInterfaceId = "19675"
        private const val humiInterfaceId = "19683"
        private const val tiltInterfaceId = "19684"
        private const val bedHeaderInterfaceId = "19685"
        private const val bedEndInterfaceId = "19686"
    }

}